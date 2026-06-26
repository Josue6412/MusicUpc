package com.example.musicupc.services;

import com.example.musicupc.entities.Reserva;
import com.example.musicupc.repositories.ReservaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public List<Reserva> listar() {
        return reservaRepository.findAll();
    }

    public Reserva listarPorId(Long id) {
        return reservaRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "La reserva con id " + id + " no existe."));
    }

    public Reserva registrar(Reserva reserva) {
        reserva.setEstado("PENDING");
        return reservaRepository.save(reserva);
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
        // Devuelve la lista (vacía si no tiene reservas). Antes lanzaba 404 al
        // estar vacía, lo que hacía fallar la pantalla "Mis reservas".
        return reservaRepository.buscarReservasPorUsuario(id);
    }
}