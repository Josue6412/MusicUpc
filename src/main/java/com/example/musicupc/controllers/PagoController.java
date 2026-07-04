package com.example.musicupc.controllers;

import com.example.musicupc.dtos.PagoDTOInsert;
import com.example.musicupc.dtos.PagoDTOList;
import com.example.musicupc.entities.Pago;
import com.example.musicupc.entities.Reserva;
import com.example.musicupc.services.PagoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<PagoDTOList> listar() {
        return pagoService.listar().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public PagoDTOList listarPorId(@PathVariable Long id) {
        return convertirADTO(pagoService.listarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    public PagoDTOList registrar(@RequestBody PagoDTOInsert dto) {
        Pago pago = convertirAEntidad(dto);
        return convertirADTO(pagoService.registrar(pago));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public PagoDTOList actualizar(@PathVariable Long id, @RequestBody PagoDTOInsert dto) {
        Pago pago = convertirAEntidad(dto);
        return convertirADTO(pagoService.actualizar(id, pago));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void eliminar(@PathVariable Long id) {
        pagoService.eliminar(id);
    }

    private PagoDTOList convertirADTO(Pago pago) {
        Reserva reserva = pago.getReserva();

        Long reservaId = reserva != null
                ? reserva.getId()
                : null;

        String reservaDetalle = reservaId != null
                ? "Reserva #" + reservaId
                : "-";

        Long clienteId = reserva != null && reserva.getCliente() != null
                ? reserva.getCliente().getId()
                : null;

        String clienteNombre = reserva != null && reserva.getCliente() != null
                ? reserva.getCliente().getNombre() + " " + reserva.getCliente().getApellido()
                : "Sin cliente";

        return new PagoDTOList(
                pago.getId(),
                reservaId,
                reservaDetalle,
                clienteId,
                clienteNombre,
                pago.getMonto(),
                pago.getMetodo(),
                pago.getEstado(),
                pago.getReferenciaTransaccion(),
                pago.getFechaPago(),
                pago.getFechaCreacion()
        );
    }

    private Pago convertirAEntidad(PagoDTOInsert dto) {
        Pago pago = new Pago();

        Reserva reserva = new Reserva();
        reserva.setId(dto.getReservaId());

        pago.setReserva(reserva);
        pago.setMonto(dto.getMonto());
        pago.setMetodo(dto.getMetodo());
        pago.setEstado(dto.getEstado());
        pago.setReferenciaTransaccion(dto.getReferenciaTransaccion());

        return pago;
    }
}