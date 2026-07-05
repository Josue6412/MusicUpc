package com.example.musicupc.services;

import com.example.musicupc.entities.PagoSuscripcion;
import com.example.musicupc.entities.Suscripcion;
import com.example.musicupc.entities.Usuario;
import com.example.musicupc.repositories.PagoSuscripcionRepository;
import com.example.musicupc.repositories.SuscripcionRepository;
import com.example.musicupc.repositories.UsuarioRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoSuscripcionService {

    private final PagoSuscripcionRepository pagoSuscripcionRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final UsuarioRepository usuarioRepository;

    public PagoSuscripcionService(
            PagoSuscripcionRepository pagoSuscripcionRepository,
            SuscripcionRepository suscripcionRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.pagoSuscripcionRepository = pagoSuscripcionRepository;
        this.suscripcionRepository = suscripcionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<PagoSuscripcion> listar() {
        return pagoSuscripcionRepository.findAll();
    }

    public List<PagoSuscripcion> listarPorUsuario(
            Long usuarioId,
            Authentication authentication
    ) {
        validarAccesoUsuario(usuarioId, authentication);

        return pagoSuscripcionRepository
                .findBySuscripcionUsuarioIdOrderByFechaCreacionDesc(usuarioId);
    }

    public PagoSuscripcion listarPorId(Long id) {
        return pagoSuscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago de suscripción no encontrado"));
    }

    @Transactional
    public PagoSuscripcion registrar(
            PagoSuscripcion pago,
            Authentication authentication
    ) {
        validarDatosBasicos(pago);

        String metodo = normalizarMetodoPago(pago.getMetodo());
        Suscripcion suscripcion = obtenerSuscripcion(pago);

        validarAccesoASuscripcion(suscripcion, authentication);
        validarSuscripcionNoCancelada(suscripcion);

        String planSolicitado = normalizarPlan(
                pago.getTipoPlan() != null && !pago.getTipoPlan().isBlank()
                        ? pago.getTipoPlan()
                        : suscripcion.getTipo_plan()
        );

        BigDecimal precioPlan = obtenerPrecioPorPlan(planSolicitado);

        validarMontoMinimo(pago, precioPlan);

        pago.setSuscripcion(suscripcion);
        pago.setMetodo(metodo);
        pago.setTipoPlan(planSolicitado);
        pago.setEstado("COMPLETADO");
        pago.setFechaPago(LocalDateTime.now());
        pago.setFechaCreacion(LocalDateTime.now());

        suscripcion.setTipo_plan(planSolicitado);
        suscripcion.setPrecio(precioPlan.doubleValue());
        suscripcion.setEstado("PAGADA");
        suscripcion.setFecha_inicio(LocalDate.now());
        suscripcion.setFecha_fin(LocalDate.now().plusMonths(1));

        suscripcionRepository.save(suscripcion);

        return pagoSuscripcionRepository.save(pago);
    }

    @Transactional
    public PagoSuscripcion actualizar(Long id, PagoSuscripcion pagoActualizado) {
        PagoSuscripcion pagoExistente = listarPorId(id);

        validarDatosBasicos(pagoActualizado);

        String metodo = normalizarMetodoPago(pagoActualizado.getMetodo());
        Suscripcion suscripcion = obtenerSuscripcion(pagoActualizado);

        if (pagoExistente.getSuscripcion() != null
                && pagoExistente.getSuscripcion().getId() != null
                && !pagoExistente.getSuscripcion().getId().equals(suscripcion.getId())) {
            throw new RuntimeException("No se puede cambiar la suscripción de un pago existente");
        }

        String planSolicitado = normalizarPlan(
                pagoActualizado.getTipoPlan() != null && !pagoActualizado.getTipoPlan().isBlank()
                        ? pagoActualizado.getTipoPlan()
                        : suscripcion.getTipo_plan()
        );

        BigDecimal precioPlan = obtenerPrecioPorPlan(planSolicitado);

        validarMontoMinimo(pagoActualizado, precioPlan);

        pagoExistente.setSuscripcion(suscripcion);
        pagoExistente.setMonto(pagoActualizado.getMonto());
        pagoExistente.setTipoPlan(planSolicitado);
        pagoExistente.setMetodo(metodo);
        pagoExistente.setReferenciaTransaccion(pagoActualizado.getReferenciaTransaccion());

        return pagoSuscripcionRepository.save(pagoExistente);
    }

    public void eliminar(Long id) {
        PagoSuscripcion pago = listarPorId(id);
        pagoSuscripcionRepository.delete(pago);
    }

    private void validarDatosBasicos(PagoSuscripcion pago) {
        if (pago.getSuscripcion() == null || pago.getSuscripcion().getId() == null) {
            throw new RuntimeException("La suscripción es obligatoria para registrar el pago");
        }

        if (pago.getMonto() == null) {
            throw new RuntimeException("El monto es obligatorio");
        }
    }

    private Suscripcion obtenerSuscripcion(PagoSuscripcion pago) {
        return suscripcionRepository.findById(pago.getSuscripcion().getId())
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));
    }

    private void validarAccesoUsuario(
            Long usuarioId,
            Authentication authentication
    ) {
        if (esAdministrador(authentication)) {
            return;
        }

        Usuario usuarioAutenticado = obtenerUsuarioAutenticado(authentication);

        if (!usuarioAutenticado.getId().equals(usuarioId)) {
            throw new AccessDeniedException("No puedes consultar pagos de otro usuario");
        }
    }

    private void validarAccesoASuscripcion(
            Suscripcion suscripcion,
            Authentication authentication
    ) {
        if (esAdministrador(authentication)) {
            return;
        }

        Usuario usuarioAutenticado = obtenerUsuarioAutenticado(authentication);

        if (suscripcion.getUsuario() == null || suscripcion.getUsuario().getId() == null) {
            throw new AccessDeniedException("La suscripción no tiene usuario asignado");
        }

        if (!suscripcion.getUsuario().getId().equals(usuarioAutenticado.getId())) {
            throw new AccessDeniedException("No puedes pagar una suscripción de otro usuario");
        }
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("Usuario autenticado no encontrado"));
    }

    private boolean esAdministrador(Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
    }

    private void validarSuscripcionNoCancelada(Suscripcion suscripcion) {
        if (suscripcion.getEstado() == null || suscripcion.getEstado().isBlank()) {
            return;
        }

        String estado = suscripcion.getEstado().trim().toUpperCase();

        if (estado.equals("CANCELADA") || estado.equals("CANCELADO")) {
            throw new RuntimeException("No se puede pagar una suscripción cancelada");
        }
    }

    private void validarMontoMinimo(PagoSuscripcion pago, BigDecimal precioPlan) {
        if (pago.getMonto().compareTo(precioPlan) < 0) {
            throw new RuntimeException("El monto no puede ser menor al precio de la suscripción");
        }
    }

    private String normalizarMetodoPago(String metodo) {
        if (metodo == null || metodo.isBlank()) {
            throw new RuntimeException("El método de pago es obligatorio");
        }

        String metodoNormalizado = metodo.trim().toUpperCase();

        if (!metodoNormalizado.equals("TARJETA")
                && !metodoNormalizado.equals("YAPE")
                && !metodoNormalizado.equals("PLIN")) {
            throw new RuntimeException("Método de pago inválido. Solo se permite TARJETA, YAPE o PLIN");
        }

        return metodoNormalizado;
    }

    private String normalizarPlan(String plan) {
        if (plan == null || plan.isBlank()) {
            throw new RuntimeException("El plan es obligatorio");
        }

        String normalizado = plan.trim().toUpperCase();

        if (normalizado.equals("BÁSICO")) {
            return "BASICO";
        }

        if (!normalizado.equals("BASICO")
                && !normalizado.equals("PREMIUM")
                && !normalizado.equals("PRO")) {
            throw new RuntimeException("Plan inválido. Solo se permite BASICO, PREMIUM o PRO");
        }

        return normalizado;
    }

    private BigDecimal obtenerPrecioPorPlan(String plan) {
        return switch (normalizarPlan(plan)) {
            case "BASICO" -> BigDecimal.valueOf(19.90);
            case "PREMIUM" -> BigDecimal.valueOf(29.90);
            case "PRO" -> BigDecimal.valueOf(49.90);
            default -> throw new RuntimeException("Plan inválido");
        };
    }
}