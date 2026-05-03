package com.example.musicupc.controllers;

import com.example.musicupc.dtos.PagoDTOInsert;
import com.example.musicupc.dtos.PagoDTOList;
import com.example.musicupc.entities.Pago;
import com.example.musicupc.entities.Reserva;
import com.example.musicupc.services.PagoService;
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
    public List<PagoDTOList> listar() {
        return pagoService.listar().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PagoDTOList listarPorId(@PathVariable Long id) {
        return convertirADTO(pagoService.listarPorId(id));
    }

    @PostMapping
    public PagoDTOList registrar(@RequestBody PagoDTOInsert dto) {
        Pago pago = convertirAEntidad(dto);
        return convertirADTO(pagoService.registrar(pago));
    }

    @PutMapping("/{id}")
    public PagoDTOList actualizar(@PathVariable Long id, @RequestBody PagoDTOInsert dto) {
        Pago pago = convertirAEntidad(dto);
        return convertirADTO(pagoService.actualizar(id, pago));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        pagoService.eliminar(id);
    }

    private PagoDTOList convertirADTO(Pago pago) {
        return new PagoDTOList(
                pago.getId(),
                pago.getReserva().getId(),
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