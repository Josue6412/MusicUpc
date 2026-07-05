package com.example.musicupc.services;

import com.example.musicupc.entities.Suscripcion;
import com.example.musicupc.entities.Usuario;
import com.example.musicupc.repositories.SuscripcionRepository;
import com.example.musicupc.repositories.UsuarioRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SuscripcionService {
    private final SuscripcionRepository suscripcionRepo;
    private final UsuarioRepository usuarioRepository;

    public SuscripcionService(
            SuscripcionRepository suscripcionRepo,
            UsuarioRepository usuarioRepository
    ) {
        this.suscripcionRepo = suscripcionRepo;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Suscripcion> listar() {
        List<Suscripcion> rows = suscripcionRepo.findAll();
        rows.forEach(this::actualizarEstadoSiVencida);
        return rows;
    }

    public Suscripcion listarPorId(Long id) {
        return suscripcionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("La suscripción con id: " + id + " no existe."));
    }

    public List<Suscripcion> listarPorUsuario(
            Long usuarioId,
            Authentication authentication
    ) {
        validarAccesoUsuario(usuarioId, authentication);

        List<Suscripcion> rows = suscripcionRepo.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
        rows.forEach(this::actualizarEstadoSiVencida);

        return rows;
    }

    public Suscripcion registrar(
            Suscripcion suscripcion,
            Authentication authentication
    ) {
        Usuario usuarioAsignado = resolverUsuarioAsignado(suscripcion, authentication);

        String planNormalizado = normalizarPlan(suscripcion.getTipo_plan());

        suscripcion.setUsuario(usuarioAsignado);
        suscripcion.setTipo_plan(planNormalizado);
        suscripcion.setPrecio(obtenerPrecioPorPlan(planNormalizado));

        if (suscripcion.getFecha_inicio() == null) {
            suscripcion.setFecha_inicio(LocalDate.now());
        }

        if (suscripcion.getFecha_fin() == null) {
            suscripcion.setFecha_fin(suscripcion.getFecha_inicio().plusMonths(1));
        }

        if (suscripcion.getFecha_fin().isBefore(suscripcion.getFecha_inicio())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        if (suscripcion.getFecha_creacion() == null) {
            suscripcion.setFecha_creacion(LocalDate.now());
        }

        if (esAdministrador(authentication)) {
            suscripcion.setEstado(normalizarEstado(suscripcion.getEstado()));
        } else {
            suscripcion.setEstado("PENDIENTE");
        }

        return suscripcionRepo.save(suscripcion);
    }

    public Suscripcion actualizar(Long id, Suscripcion suscripcion) {
        Suscripcion existente = listarPorId(id);

        String planNormalizado = normalizarPlan(suscripcion.getTipo_plan());

        existente.setTipo_plan(planNormalizado);
        existente.setPrecio(obtenerPrecioPorPlan(planNormalizado));
        existente.setFecha_inicio(suscripcion.getFecha_inicio());
        existente.setFecha_fin(suscripcion.getFecha_fin());
        existente.setEstado(normalizarEstado(suscripcion.getEstado()));

        return suscripcionRepo.save(existente);
    }

    public void eliminar(Long id) {
        Suscripcion suscripcion = listarPorId(id);
        suscripcionRepo.delete(suscripcion);
    }

    private Usuario resolverUsuarioAsignado(
            Suscripcion suscripcion,
            Authentication authentication
    ) {
        if (esAdministrador(authentication)) {
            if (suscripcion.getUsuario() == null || suscripcion.getUsuario().getId() == null) {
                throw new RuntimeException("El usuario es obligatorio para registrar la suscripción");
            }

            return usuarioRepository.findById(suscripcion.getUsuario().getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        }

        Usuario usuarioAutenticado = obtenerUsuarioAutenticado(authentication);

        if (suscripcion.getUsuario() != null && suscripcion.getUsuario().getId() != null) {
            if (!suscripcion.getUsuario().getId().equals(usuarioAutenticado.getId())) {
                throw new AccessDeniedException("No puedes crear una suscripción para otro usuario");
            }
        }

        return usuarioAutenticado;
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
            throw new AccessDeniedException("No puedes consultar la suscripción de otro usuario");
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

    private String normalizarPlan(String plan) {
        if (plan == null || plan.isBlank()) {
            throw new IllegalArgumentException("El tipo de plan es obligatorio.");
        }

        String normalizado = plan.trim().toUpperCase();

        if (normalizado.equals("BÁSICO")) {
            return "BASICO";
        }

        if (!normalizado.equals("BASICO")
                && !normalizado.equals("PREMIUM")
                && !normalizado.equals("PRO")) {
            throw new IllegalArgumentException("Plan inválido. Solo se permite BASICO, PREMIUM o PRO.");
        }

        return normalizado;
    }

    private double obtenerPrecioPorPlan(String plan) {
        return switch (normalizarPlan(plan)) {
            case "BASICO" -> 19.90;
            case "PREMIUM" -> 29.90;
            case "PRO" -> 49.90;
            default -> throw new IllegalArgumentException("Plan inválido.");
        };
    }

    private String normalizarEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return "PENDIENTE";
        }

        String normalizado = estado.trim().toUpperCase();

        if (normalizado.equals("PENDING")) {
            return "PENDIENTE";
        }

        if (!normalizado.equals("PENDIENTE")
                && !normalizado.equals("PAGADA")
                && !normalizado.equals("VENCIDA")
                && !normalizado.equals("CANCELADA")) {
            throw new IllegalArgumentException("Estado de suscripción inválido.");
        }

        return normalizado;
    }

    private void actualizarEstadoSiVencida(Suscripcion s) {
        if (s.getFecha_fin() != null
                && s.getFecha_fin().isBefore(LocalDate.now())
                && "PAGADA".equalsIgnoreCase(s.getEstado())) {
            s.setEstado("VENCIDA");
            suscripcionRepo.save(s);
        }
    }
}