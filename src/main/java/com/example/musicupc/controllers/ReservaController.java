package com.example.musicupc.controllers;

import com.example.musicupc.dtos.ReservaDTOInsert;
import com.example.musicupc.dtos.ReservaDTOList;
import com.example.musicupc.entities.Reserva;
import com.example.musicupc.entities.Usuario;
import com.example.musicupc.services.ReservaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public List<ReservaDTOList> listar() {
        return reservaService.listar().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ReservaDTOList listarPorId(@PathVariable Long id) {
        return convertirADTO(reservaService.listarPorId(id));
    }

    @PostMapping
    public ReservaDTOList registrar(@RequestBody ReservaDTOInsert dto) {
        Reserva reserva = convertirAEntidad(dto);
        return convertirADTO(reservaService.registrar(reserva));
    }

    @PutMapping("/{id}")
    public ReservaDTOList actualizar(@PathVariable Long id, @RequestBody ReservaDTOInsert dto) {
        Reserva reserva = convertirAEntidad(dto);
        return convertirADTO(reservaService.actualizar(id, reserva));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);
    }

    private ReservaDTOList convertirADTO(Reserva reserva) {
        return new ReservaDTOList(
                reserva.getId(),
                reserva.getCliente().getId(),
                reserva.getFechaEvento(),
                reserva.getHoraEvento(),
                reserva.getUbicacionEvento(),
                reserva.getTipoEvento(),
                reserva.getDuracionHoras(),
                reserva.getPrecioTotal(),
                reserva.getEstado(),
                reserva.getNotas(),
                reserva.getFechaCreacion(),
                reserva.getFechaActualizacion()
        );
    }

    private Reserva convertirAEntidad(ReservaDTOInsert dto) {
        Reserva reserva = new Reserva();

        Usuario cliente = new Usuario();
        cliente.setId(dto.getClienteId());
        reserva.setCliente(cliente);

        reserva.setFechaEvento(dto.getFechaEvento());
        reserva.setHoraEvento(dto.getHoraEvento());
        reserva.setUbicacionEvento(dto.getUbicacionEvento());
        reserva.setTipoEvento(dto.getTipoEvento());
        reserva.setDuracionHoras(dto.getDuracionHoras());
        reserva.setPrecioTotal(dto.getPrecioTotal());
        reserva.setNotas(dto.getNotas());

        return reserva;
    }
}