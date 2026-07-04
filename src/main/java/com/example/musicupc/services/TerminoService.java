package com.example.musicupc.services;

import com.example.musicupc.entities.Reserva;
import com.example.musicupc.entities.Terminos;
import com.example.musicupc.repositories.ReservaRepository;
import com.example.musicupc.repositories.TerminoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        return terminoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el término con id: " + id));
    }

    public Terminos registrar(Terminos termino, Long reservaId) {
        Reserva reserva = reservaRepo.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (terminoRepo.existsByReservaId_Id(reservaId)) {
            throw new RuntimeException("Ya existe un término registrado para la reserva #" + reservaId);
        }

        termino.setReservaId(reserva);

        if (termino.getStatus() == null || termino.getStatus().isBlank()) {
            termino.setStatus("PENDIENTE");
        }

        if (termino.getFecha_creacion() == null) {
            termino.setFecha_creacion(LocalDate.now());
        }

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

    public Terminos crearAutomaticoParaReserva(Reserva reserva) {
        if (reserva == null || reserva.getId() == null) {
            throw new RuntimeException("La reserva es obligatoria para generar términos");
        }

        return terminoRepo.findByReservaId_Id(reserva.getId())
                .orElseGet(() -> {
                    Terminos termino = new Terminos();
                    termino.setReservaId(reserva);
                    termino.setTerminos(generarTextoTerminos(reserva));
                    termino.setStatus("PENDIENTE");
                    termino.setFecha_creacion(LocalDate.now());
                    termino.setFecha_confirmacion(null);

                    return terminoRepo.save(termino);
                });
    }

    public Terminos confirmarPorReserva(Long reservaId) {
        Reserva reserva = reservaRepo.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        Terminos termino = terminoRepo.findByReservaId_Id(reservaId)
                .orElseGet(() -> crearAutomaticoParaReserva(reserva));

        termino.setStatus("CONFIRMADO");
        termino.setFecha_confirmacion(LocalDate.now());

        return terminoRepo.save(termino);
    }

    private String generarTextoTerminos(Reserva reserva) {
        return "El cliente acepta las condiciones del servicio musical solicitado. "
                + "La reserva corresponde a un evento de tipo "
                + reserva.getTipoEvento()
                + ", ubicado en "
                + reserva.getUbicacionEvento()
                + ", programado para la fecha "
                + reserva.getFechaEvento()
                + " a las "
                + reserva.getHoraEvento()
                + ", con una duración de "
                + reserva.getDuracionHoras()
                + " hora(s) y un precio total de S/ "
                + reserva.getPrecioTotal()
                + ".";
    }

    public void eliminar(Long id) {
        Terminos termino = listarPorId(id);
        terminoRepo.delete(termino);
    }
}