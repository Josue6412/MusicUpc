package com.example.musicupc.controllers;

import com.example.musicupc.dtos.ReseñaDTOInsert;
import com.example.musicupc.dtos.ReseñaDTOList;
import com.example.musicupc.entities.Reseña;
import com.example.musicupc.entities.Reserva;
import com.example.musicupc.entities.Usuario;
import com.example.musicupc.services.ReseñaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reseñas")
public class ReseñaController {

    private final ReseñaService reseñaService;

    public ReseñaController(ReseñaService reseñaService) {
        this.reseñaService = reseñaService;
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    public List<ReseñaDTOList> listarPorUsuario(@PathVariable Long usuarioId, Authentication authentication) {
        return reseñaService.listarPorUsuario(usuarioId, authentication)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/reserva/{reservaId}/promedio")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Double obtenerPromedioRatingPorReserva(@PathVariable Long reservaId) {
        return reseñaService.obtenerPromedioRatingPorReserva(reservaId);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<ReseñaDTOList> listar() {
        return reseñaService.listar().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ReseñaDTOList listarPorId(@PathVariable Long id) {
        return convertirADTO(reseñaService.listarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    public ReseñaDTOList registrar(@RequestBody ReseñaDTOInsert dto, Authentication authentication) {
        Reseña reseña = convertirAEntidad(dto);
        return convertirADTO(reseñaService.registrar(reseña, authentication));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ReseñaDTOList actualizar(@PathVariable Long id, @RequestBody ReseñaDTOInsert dto) {
        Reseña reseña = convertirAEntidad(dto);
        return convertirADTO(reseñaService.actualizar(id, reseña));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void eliminar(@PathVariable Long id) {
        reseñaService.eliminar(id);
    }

    private ReseñaDTOList convertirADTO(Reseña reseña) {
        String nombreCompleto = "Sin usuario";

        if (reseña.getUsuario() != null) {
            nombreCompleto = reseña.getUsuario().getNombre() + " " + reseña.getUsuario().getApellido();
        }

        Long reservaId = reseña.getReserva() != null
                ? reseña.getReserva().getId()
                : null;

        String reservaDetalle = reservaId != null
                ? "Reserva #" + reservaId
                : "-";

        String artistaNombre = reseña.getReserva() != null
                && reseña.getReserva().getArtista() != null
                ? reseña.getReserva().getArtista().getNombreArtistico()
                : "Sin artista";

        return new ReseñaDTOList(
                reseña.getId(),
                reseña.getUsuario() != null ? reseña.getUsuario().getId() : null,
                nombreCompleto,
                reservaId,
                reservaDetalle,
                artistaNombre,
                reseña.getRating(),
                reseña.getComentario(),
                reseña.getFechaCreacion()
        );
    }

    private Reseña convertirAEntidad(ReseñaDTOInsert dto) {
        Reseña reseña = new Reseña();

        Usuario usuario = new Usuario();
        usuario.setId(dto.getUsuarioId());

        Reserva reserva = new Reserva();
        reserva.setId(dto.getReservaId());

        reseña.setUsuario(usuario);
        reseña.setReserva(reserva);
        reseña.setRating(dto.getRating());
        reseña.setComentario(dto.getComentario());

        return reseña;
    }
}