package com.example.musicupc.controllers;

import com.example.musicupc.dtos.TerminoDTOInsert;
import com.example.musicupc.dtos.TerminoDTOList;
import com.example.musicupc.entities.Terminos;
import com.example.musicupc.services.TerminoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/terminos")
public class TerminoController {

    private final TerminoService terminoService;

    public TerminoController(TerminoService terminoService) {
        this.terminoService = terminoService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<TerminoDTOList> listar() {
        return terminoService.listar().stream().map(this::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public TerminoDTOList listarPorId(@PathVariable Long id) {
        return convertToDTO(terminoService.listarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public TerminoDTOList insertar(@RequestBody TerminoDTOInsert dto) {
        Terminos termino = convertToEntity(dto);
        return convertToDTO(terminoService.registrar(termino, dto.getReservaId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public TerminoDTOList actualizar(@PathVariable Long id, @RequestBody TerminoDTOInsert dto) {
        Terminos termino = convertToEntity(dto);
        return convertToDTO(terminoService.actualizar(id, termino, dto.getReservaId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void eliminar(@PathVariable Long id) {
        terminoService.eliminar(id);
    }

    private TerminoDTOList convertToDTO(Terminos termino) {
        Long reservaId = termino.getReservaId() != null
                ? termino.getReservaId().getId()
                : null;

        String reservaDetalle = reservaId != null
                ? "Reserva #" + reservaId
                : "-";

        Long clienteId = termino.getReservaId() != null
                && termino.getReservaId().getCliente() != null
                ? termino.getReservaId().getCliente().getId()
                : null;

        String clienteNombre = termino.getReservaId() != null
                && termino.getReservaId().getCliente() != null
                ? termino.getReservaId().getCliente().getNombre() + " " + termino.getReservaId().getCliente().getApellido()
                : "Sin cliente";

        return new TerminoDTOList(
                termino.getId(),
                reservaId,
                reservaDetalle,
                clienteId,
                clienteNombre,
                termino.getTerminos(),
                termino.getStatus(),
                termino.getFecha_confirmacion(),
                termino.getFecha_creacion()
        );
    }

    private Terminos convertToEntity(TerminoDTOInsert dto) {
        Terminos termino = new Terminos();
        termino.setTerminos(dto.getTerminos());
        termino.setStatus(dto.getStatus());
        termino.setFecha_confirmacion(dto.getFecha_confirmacion());
        termino.setFecha_creacion(dto.getFecha_creacion());
        return termino;
    }
}
