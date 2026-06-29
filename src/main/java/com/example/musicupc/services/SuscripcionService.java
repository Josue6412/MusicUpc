package com.example.musicupc.services;

import com.example.musicupc.entities.Suscripcion;
import com.example.musicupc.repositories.SuscripcionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SuscripcionService {
    private final SuscripcionRepository suscripcionRepo;

    public SuscripcionService(SuscripcionRepository suscripcionRepo) {
        this.suscripcionRepo = suscripcionRepo;
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

    public List<Suscripcion> listarPorUsuario(Long usuarioId) {
        List<Suscripcion> rows = suscripcionRepo.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
        rows.forEach(this::actualizarEstadoSiVencida);
        return rows;
    }

    public Suscripcion registrar(Suscripcion suscripcion) {
        if (suscripcion.getTipo_plan() == null || suscripcion.getTipo_plan().isBlank()) {
            throw new IllegalArgumentException("El tipo de plan es obligatorio.");
        }

        if (suscripcion.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }

        if (suscripcion.getFecha_inicio() == null) {
            suscripcion.setFecha_inicio(LocalDate.now());
        }

        if (suscripcion.getFecha_inicio() == null) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria.");
        }

        if (suscripcion.getFecha_fin() == null) {
            suscripcion.setFecha_fin(suscripcion.getFecha_inicio().plusMonths(1));
        }

        if (suscripcion.getFecha_fin() != null && suscripcion.getFecha_fin().isBefore(suscripcion.getFecha_inicio())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        if (suscripcion.getEstado() == null || suscripcion.getEstado().isBlank()) {
            throw new IllegalArgumentException("El estado de la suscripción es obligatorio.");
        }

        if (suscripcion.getFecha_creacion() == null) {
            suscripcion.setFecha_creacion(LocalDate.now());
        }

        return suscripcionRepo.save(suscripcion);
    }

    public Suscripcion actualizar(Long id, Suscripcion suscripcion) {
        Suscripcion existente = listarPorId(id);

        existente.setTipo_plan(suscripcion.getTipo_plan());
        existente.setPrecio(suscripcion.getPrecio());
        existente.setFecha_inicio(suscripcion.getFecha_inicio());
        existente.setFecha_fin(suscripcion.getFecha_fin());
        existente.setEstado(suscripcion.getEstado());

        // Nota: La fecha_creacion normalmente no se actualiza por ser auditoría de creación

        return suscripcionRepo.save(existente);
    }

    public void eliminar(Long id) {
        Suscripcion suscripcion = listarPorId(id);
        suscripcionRepo.delete(suscripcion);
    }

    private void actualizarEstadoSiVencida(Suscripcion s) {
        if (s.getFecha_fin() != null
                && s.getFecha_fin().isBefore(LocalDate.now())
                && "PAGADA".equalsIgnoreCase(s.getEstado())) {
            s.setEstado("PENDIENTE");
            suscripcionRepo.save(s);
        }
    }
}