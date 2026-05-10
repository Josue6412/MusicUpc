package com.example.musicupc.controllers;

import com.example.musicupc.dtos.SuscripcionDTOInsert;
import com.example.musicupc.dtos.SuscripcionDTOList;
import com.example.musicupc.entities.Suscripcion;
import com.example.musicupc.entities.Usuario;
import com.example.musicupc.services.SuscripcionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/suscripciones")
public class SuscripcionController {

    private final SuscripcionService suscripcionService;

    public SuscripcionController(SuscripcionService suscripcionService) {
        this.suscripcionService = suscripcionService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<SuscripcionDTOList> listar() {
        return suscripcionService.listar().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public SuscripcionDTOList listarPorId(@PathVariable Long id) {
        return convertirADTO(suscripcionService.listarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public SuscripcionDTOList registrar(@RequestBody SuscripcionDTOInsert dto) {
        Suscripcion suscripcion = convertirAEntidad(dto);

        // Seteamos valores automáticos para el registro inicial
        suscripcion.setFecha_inicio(LocalDate.now());
        suscripcion.setEstado("ACTIVO");
        suscripcion.setFecha_creacion(LocalDate.now());

        return convertirADTO(suscripcionService.registrar(suscripcion));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public SuscripcionDTOList actualizar(@PathVariable Long id, @RequestBody SuscripcionDTOInsert dto) {
        Suscripcion suscripcion = convertirAEntidad(dto);
        return convertirADTO(suscripcionService.actualizar(id, suscripcion));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void eliminar(@PathVariable Long id) {
        suscripcionService.eliminar(id);
    }

    // --- Métodos de conversión (Mappers) ---

    private SuscripcionDTOList convertirADTO(Suscripcion s) {
        SuscripcionDTOList dto = new SuscripcionDTOList();

        dto.setId(s.getId());
        // Asumiendo que tu entidad Suscripcion tiene una relación con Usuario
        dto.setUsuario_id(s.getUsuario() != null ? s.getUsuario().getId() : null);
        dto.setTipo_plan(s.getTipo_plan());
        dto.setPrecio(s.getPrecio());
        dto.setFecha_inicio(s.getFecha_inicio());
        dto.setFecha_fin(s.getFecha_fin());
        dto.setEstado(s.getEstado());
        dto.setFecha_creacion(s.getFecha_creacion());

        return dto;
    }

    private Suscripcion convertirAEntidad(SuscripcionDTOInsert dto) {
        Suscripcion suscripcion = new Suscripcion();

        suscripcion.setTipo_plan(dto.getTipo_plan());
        suscripcion.setPrecio(dto.getPrecio());
        suscripcion.setFecha_fin(dto.getFecha_fin());

        // Si el DTO de actualización envía el estado, lo mapeamos
        if (dto.getEstado() != null) {
            suscripcion.setEstado(dto.getEstado());
        }

        if (dto.getUsuario_id() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(dto.getUsuario_id());
            suscripcion.setUsuario(usuario);
        }

        return suscripcion;
    }
}
