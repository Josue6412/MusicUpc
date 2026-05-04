package com.example.musicupc.controllers;

import com.example.musicupc.dtos.ReseñaDTOInsert;
import com.example.musicupc.dtos.ReseñaDTOList;
import com.example.musicupc.entities.Reseña;
import com.example.musicupc.entities.Reserva;
import com.example.musicupc.entities.Usuario;
import com.example.musicupc.services.ReseñaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reseñas")
public class ReseñaController {

    private final ReseñaService reseñaService;

    public ReseñaController(ReseñaService reseñaService) {
        this.reseñaService = reseñaService;
    }

    @GetMapping
    public List<ReseñaDTOList> listar() {
        return reseñaService.listar().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ReseñaDTOList listarPorId(@PathVariable Long id) {
        return convertirADTO(reseñaService.listarPorId(id));
    }

    @PostMapping
    public ReseñaDTOList registrar(@RequestBody ReseñaDTOInsert dto) {
        Reseña reseña = convertirAEntidad(dto);
        return convertirADTO(reseñaService.registrar(reseña));
    }

    @PutMapping("/{id}")
    public ReseñaDTOList actualizar(@PathVariable Long id, @RequestBody ReseñaDTOInsert dto) {
        Reseña reseña = convertirAEntidad(dto);
        return convertirADTO(reseñaService.actualizar(id, reseña));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        reseñaService.eliminar(id);
    }

    private ReseñaDTOList convertirADTO(Reseña reseña) {
        String nombreCompleto = null;
        if (reseña.getUsuario() != null) {
            nombreCompleto = reseña.getUsuario().getNombre() + " " + reseña.getUsuario().getApellido();
        }

        return new ReseñaDTOList(
                reseña.getId(),
                reseña.getUsuario() != null ? reseña.getUsuario().getId() : null,
                nombreCompleto,
                reseña.getReserva() != null ? reseña.getReserva().getId() : null,
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