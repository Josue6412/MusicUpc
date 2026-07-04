package com.example.musicupc.services;

import com.example.musicupc.entities.Reserva;
import com.example.musicupc.repositories.ReservaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final TerminoService terminoService;

    public ReservaService(ReservaRepository reservaRepository, TerminoService terminoService) {
        this.reservaRepository = reservaRepository;
        this.terminoService = terminoService;
    }

    public List<Reserva> listar() {
        return reservaRepository.findAll();
    }

    public Reserva listarPorId(Long id) {
        return reservaRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "La reserva con id " + id + " no existe."));
    }

    @Transactional
    public Reserva registrar(Reserva reserva) {
        reserva.setEstado("PENDING");

        Reserva reservaGuardada = reservaRepository.save(reserva);

        terminoService.crearAutomaticoParaReserva(reservaGuardada);

        return reservaGuardada;
    }

    public Reserva actualizar(Long id, Reserva reservaDetalles) {
        Reserva reservaExistente = listarPorId(id);

        reservaExistente.setCliente(reservaDetalles.getCliente());
        reservaExistente.setFechaEvento(reservaDetalles.getFechaEvento());
        reservaExistente.setHoraEvento(reservaDetalles.getHoraEvento());
        reservaExistente.setUbicacionEvento(reservaDetalles.getUbicacionEvento());
        reservaExistente.setTipoEvento(reservaDetalles.getTipoEvento());
        reservaExistente.setDuracionHoras(reservaDetalles.getDuracionHoras());
        reservaExistente.setPrecioTotal(reservaDetalles.getPrecioTotal());
        reservaExistente.setNotas(reservaDetalles.getNotas());

        return reservaRepository.save(reservaExistente);
    }

    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }

    public List<Reserva> buscarReservasPorUsuario(Long id) {
        return reservaRepository.buscarReservasPorUsuario(id);
    }
}