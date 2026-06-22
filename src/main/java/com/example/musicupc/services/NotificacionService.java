package com.example.musicupc.services;

import com.example.musicupc.entities.Notificacion;
import com.example.musicupc.repositories.NotificacionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificacionService {
    private final NotificacionRepository notificacionRepo;

    public NotificacionService(NotificacionRepository notificacionRepo) {
        this.notificacionRepo = notificacionRepo;
    }

    public List<Notificacion> listar() {
        return notificacionRepo.findAll();
    }

    public Notificacion listarPorId(Long id) {
        return notificacionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("La notificación con id: " + id + " no existe."));
    }

    public Notificacion registrar(Notificacion notificacion) {
        // Validación de campos obligatorios
        if (notificacion.getUsuario() == null) {
            throw new IllegalArgumentException("El ID de usuario es obligatorio.");
        }

        if (notificacion.getTitulo() == null || notificacion.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio.");
        }
        if (notificacion.getTitulo().length() > 200) {
            throw new IllegalArgumentException("El título no puede exceder los 200 caracteres.");
        }

        if (notificacion.getMensaje() == null || notificacion.getMensaje().isBlank()) {
            throw new IllegalArgumentException("El mensaje de la notificación es obligatorio.");
        }

        if (notificacion.getTipo() == null || notificacion.getTipo().isBlank()) {
            throw new IllegalArgumentException("El tipo de notificación es obligatorio.");
        }
        if (notificacion.getTipo().length() > 50) {
            throw new IllegalArgumentException("El tipo no puede exceder los 50 caracteres.");
        }

        // Asignar fecha de creación si no viene establecida
        if (notificacion.getFecha_creacion() == null) {
            notificacion.setFecha_creacion(LocalDate.now());
        }

        return notificacionRepo.save(notificacion);
    }

    public Notificacion actualizar(Long id, Notificacion notificacion) {
        Notificacion existente = listarPorId(id);

        existente.setTitulo(notificacion.getTitulo());
        existente.setMensaje(notificacion.getMensaje());
        existente.setTipo(notificacion.getTipo());
        existente.setLeido(notificacion.isLeido()); // Ajustado a CamelCase

        // Corregido: Debes pasar el objeto Usuario, no un ID
        if (notificacion.getUsuario() != null) {
            existente.setUsuario(notificacion.getUsuario());
        }

        return notificacionRepo.save(existente);
    }

    public void eliminar(Long id) {
        Notificacion notificacion = listarPorId(id);
        notificacionRepo.delete(notificacion);
    }
}