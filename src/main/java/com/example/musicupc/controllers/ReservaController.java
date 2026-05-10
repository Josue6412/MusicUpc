package com.example.musicupc.controllers;

import com.example.musicupc.dtos.ReservaDTOInsert;
import com.example.musicupc.dtos.ReservaDTOList;
import com.example.musicupc.dtos.ReservaUsuarioDTO;
import com.example.musicupc.entities.Reserva;
import com.example.musicupc.entities.Usuario;
import com.example.musicupc.services.ReservaService;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<ReservaDTOList> listar() {
        return reservaService.listar().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ReservaDTOList listarPorId(@PathVariable Long id) {
        return convertirADTO(reservaService.listarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ReservaDTOList registrar(@RequestBody ReservaDTOInsert dto) {
        Reserva reserva = convertirAEntidad(dto);
        return convertirADTO(reservaService.registrar(reserva));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ReservaDTOList actualizar(@PathVariable Long id, @RequestBody ReservaDTOInsert dto) {
        Reserva reserva = convertirAEntidad(dto);
        return convertirADTO(reservaService.actualizar(id, reserva));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);
    }

    @GetMapping("/usuario/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<ReservaUsuarioDTO> buscarReservasPorUsuario(@PathVariable Long id) {

        return reservaService.buscarReservasPorUsuario(id)
                .stream()
                .map(this::convertirAReservaUsuarioDTO)
                .toList();
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

    private ReservaUsuarioDTO convertirAReservaUsuarioDTO(Reserva reserva) {

        return new ReservaUsuarioDTO(
                reserva.getCliente().getNombre(),
                reserva.getId(),
                reserva.getEstado(),
                reserva.getNotas(),
                reserva.getFechaEvento(),
                reserva.getHoraEvento(),
                reserva.getPrecioTotal()
        );
    }
}