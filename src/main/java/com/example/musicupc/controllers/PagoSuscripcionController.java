package com.example.musicupc.controllers;

import com.example.musicupc.dtos.PagoSuscripcionDTOInsert;
import com.example.musicupc.dtos.PagoSuscripcionDTOList;
import com.example.musicupc.entities.PagoSuscripcion;
import com.example.musicupc.entities.Suscripcion;
import com.example.musicupc.services.PagoSuscripcionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagos-suscripcion")
public class PagoSuscripcionController {

    private final PagoSuscripcionService pagoSuscripcionService;

    public PagoSuscripcionController(PagoSuscripcionService pagoSuscripcionService) {
        this.pagoSuscripcionService = pagoSuscripcionService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<PagoSuscripcionDTOList> listar() {
        return pagoSuscripcionService.listar()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    public List<PagoSuscripcionDTOList> listarPorUsuario(@PathVariable Long usuarioId) {
        return pagoSuscripcionService.listarPorUsuario(usuarioId)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    public PagoSuscripcionDTOList registrar(@RequestBody PagoSuscripcionDTOInsert dto) {
        PagoSuscripcion pago = convertirAEntidad(dto);
        return convertirADTO(pagoSuscripcionService.registrar(pago));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void eliminar(@PathVariable Long id) {
        pagoSuscripcionService.eliminar(id);
    }

    private PagoSuscripcionDTOList convertirADTO(PagoSuscripcion pago) {
        return new PagoSuscripcionDTOList(
                pago.getId(),
                pago.getSuscripcion().getId(),
                pago.getSuscripcion().getUsuario() != null
                        ? pago.getSuscripcion().getUsuario().getId()
                        : null,
                pago.getMonto(),
                pago.getTipoPlan(),
                pago.getMetodo(),
                pago.getEstado(),
                pago.getReferenciaTransaccion(),
                pago.getFechaPago(),
                pago.getFechaCreacion()
        );
    }

    private PagoSuscripcion convertirAEntidad(PagoSuscripcionDTOInsert dto) {
        PagoSuscripcion pago = new PagoSuscripcion();

        Suscripcion suscripcion = new Suscripcion();
        suscripcion.setId(dto.getSuscripcionId());

        pago.setSuscripcion(suscripcion);
        pago.setMonto(dto.getMonto());
        pago.setTipoPlan(dto.getTipoPlan());
        pago.setMetodo(dto.getMetodo());
        pago.setReferenciaTransaccion(dto.getReferenciaTransaccion());

        return pago;
    }
}