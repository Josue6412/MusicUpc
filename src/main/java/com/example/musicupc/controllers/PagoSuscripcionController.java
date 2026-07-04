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

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public PagoSuscripcionDTOList actualizar(
            @PathVariable Long id,
            @RequestBody PagoSuscripcionDTOInsert dto
    ) {
        PagoSuscripcion pago = convertirAEntidad(dto);
        return convertirADTO(pagoSuscripcionService.actualizar(id, pago));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void eliminar(@PathVariable Long id) {
        pagoSuscripcionService.eliminar(id);
    }

    private PagoSuscripcionDTOList convertirADTO(PagoSuscripcion pago) {
        Suscripcion suscripcion = pago.getSuscripcion();

        Long suscripcionId = suscripcion != null ? suscripcion.getId() : null;

        Long usuarioId = suscripcion != null && suscripcion.getUsuario() != null
                ? suscripcion.getUsuario().getId()
                : null;

        String usuarioNombre = suscripcion != null && suscripcion.getUsuario() != null
                ? suscripcion.getUsuario().getNombre() + " " + suscripcion.getUsuario().getApellido()
                : "Sin usuario";

        String plan = pago.getTipoPlan() != null
                ? pago.getTipoPlan()
                : suscripcion != null ? suscripcion.getTipo_plan() : "";

        String suscripcionDetalle = suscripcionId != null
                ? plan + " #" + suscripcionId
                : "-";

        return new PagoSuscripcionDTOList(
                pago.getId(),
                suscripcionId,
                usuarioId,
                usuarioNombre,
                suscripcionDetalle,
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