package com.example.musicupc.services;

import com.example.musicupc.entities.PagoSuscripcion;
import com.example.musicupc.entities.Suscripcion;
import com.example.musicupc.repositories.PagoSuscripcionRepository;
import com.example.musicupc.repositories.SuscripcionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoSuscripcionService {

    private final PagoSuscripcionRepository pagoSuscripcionRepository;
    private final SuscripcionRepository suscripcionRepository;

    public PagoSuscripcionService(PagoSuscripcionRepository pagoSuscripcionRepository, SuscripcionRepository suscripcionRepository) {
        this.pagoSuscripcionRepository = pagoSuscripcionRepository;
        this.suscripcionRepository = suscripcionRepository;
    }

    public List<PagoSuscripcion> listar() {
        return pagoSuscripcionRepository.findAll();
    }

    public List<PagoSuscripcion> listarPorUsuario(Long usuarioId) {
        return pagoSuscripcionRepository.findBySuscripcionUsuarioIdOrderByFechaCreacionDesc(usuarioId);
    }

    public PagoSuscripcion listarPorId(Long id) {
        return pagoSuscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago de suscripción no encontrado"));
    }

    public PagoSuscripcion registrar(PagoSuscripcion pago) {
        validarMetodoPago(pago.getMetodo());

        if (pago.getSuscripcion() == null || pago.getSuscripcion().getId() == null) {
            throw new RuntimeException("La suscripción es obligatoria para registrar el pago");
        }

        Suscripcion suscripcion = suscripcionRepository.findById(pago.getSuscripcion().getId())
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));

        if (pago.getTipoPlan() == null || pago.getTipoPlan().isBlank()) {
            pago.setTipoPlan(suscripcion.getTipo_plan());
        }

        pago.setSuscripcion(suscripcion);
        pago.setEstado("COMPLETADO");
        pago.setFechaPago(LocalDateTime.now());
        pago.setFechaCreacion(LocalDateTime.now());

        suscripcion.setEstado("PAGADA");
        suscripcion.setFecha_inicio(LocalDate.now());
        suscripcion.setFecha_fin(LocalDate.now().plusMonths(1));

        suscripcionRepository.save(suscripcion);

        return pagoSuscripcionRepository.save(pago);
    }

    private void validarMetodoPago(String metodo) {
        if (metodo == null || metodo.isBlank()) {
            throw new RuntimeException("El método de pago es obligatorio");
        }

        String metodoNormalizado = metodo.trim().toUpperCase();

        if (!metodoNormalizado.equals("TARJETA")
                && !metodoNormalizado.equals("YAPE")
                && !metodoNormalizado.equals("PLIN")) {
            throw new RuntimeException("Método de pago inválido. Solo se permite TARJETA, YAPE o PLIN");
        }
    }

    public void eliminar(Long id) {
        pagoSuscripcionRepository.deleteById(id);
    }
}