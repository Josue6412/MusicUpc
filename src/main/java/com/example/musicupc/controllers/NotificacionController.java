package com.example.musicupc.controllers;

import com.example.musicupc.dtos.NotificacionDTOInsert;
import com.example.musicupc.dtos.NotificacionDTOList;
import com.example.musicupc.entities.Notificacion;
import com.example.musicupc.entities.Usuario;
import com.example.musicupc.services.NotificacionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<NotificacionDTOList> listar() {
        return notificacionService.listar().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public NotificacionDTOList listarPorId(@PathVariable Long id) {
        return convertirADTO(notificacionService.listarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public NotificacionDTOList registrar(@RequestBody NotificacionDTOInsert dto) {
        Notificacion notificacion = convertirAEntidad(dto);
        // Seteamos la fecha de creación actual al registrar
        notificacion.setFecha_creacion(LocalDate.now());
        notificacion.setLeido(false); // Por defecto no leída
        return convertirADTO(notificacionService.registrar(notificacion));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public NotificacionDTOList actualizar(@PathVariable Long id, @RequestBody NotificacionDTOInsert dto) {
        Notificacion notificacion = convertirAEntidad(dto);
        return convertirADTO(notificacionService.actualizar(id, notificacion));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void eliminar(@PathVariable Long id) {
        notificacionService.eliminar(id);
    }

    // --- Métodos de conversión ---

    private NotificacionDTOList convertirADTO(Notificacion n) {
        return new NotificacionDTOList(
                n.getId(),
                n.getUsuario() != null ? n.getUsuario().getId() : null,
                n.getTitulo(),
                n.getMensaje(),
                n.getTipo(),
                n.isLeido(),
                n.getFecha_creacion()

        );
    }

    private Notificacion convertirAEntidad(NotificacionDTOInsert dto) {
        Notificacion notificacion = new Notificacion();

        notificacion.setTitulo(dto.getTitulo());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setTipo(dto.getTipo());

        if (dto.getUsuario_id() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(dto.getUsuario_id());
            notificacion.setUsuario(usuario);
        }

        return notificacion;
    }
}