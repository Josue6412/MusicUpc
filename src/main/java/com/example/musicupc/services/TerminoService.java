package com.example.musicupc.services;

import com.example.musicupc.entities.Terminos;
import com.example.musicupc.entities.Reserva;
import com.example.musicupc.repositories.TerminoRepository;
import com.example.musicupc.repositories.ReservaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TerminoService {
    private final TerminoRepository terminoRepo;
    private final ReservaRepository reservaRepo;

    public TerminoService(TerminoRepository terminoRepo, ReservaRepository reservaRepo) {
        this.terminoRepo = terminoRepo;
        this.reservaRepo = reservaRepo;
    }

    public List<Terminos> listar() {
        return terminoRepo.findAll();
    }

    public Terminos listarPorId(Long id) {
        return terminoRepo.findById(id).orElseThrow(() -> new RuntimeException("No existe el término con id: " + id));
    }

    public Terminos registrar(Terminos termino, Long reservaId) {
        Reserva reserva = reservaRepo.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        termino.setReservaId(reserva);

        return terminoRepo.save(termino);
    }

    public Terminos actualizar(Long id, Terminos termino, Long reservaId) {
        Terminos existente = listarPorId(id);

        existente.setTerminos(termino.getTerminos());
        existente.setStatus(termino.getStatus());
        existente.setFecha_confirmacion(termino.getFecha_confirmacion());
        existente.setFecha_creacion(termino.getFecha_creacion());

        Reserva reserva = reservaRepo.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        existente.setReservaId(reserva);

        return terminoRepo.save(existente);
    }

    public void eliminar(Long id) {
        Terminos termino = listarPorId(id);
        terminoRepo.delete(termino);
    }
}
