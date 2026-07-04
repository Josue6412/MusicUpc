package com.example.musicupc.services;

import com.example.musicupc.entities.Reseña;
import com.example.musicupc.entities.Reserva;
import com.example.musicupc.entities.Usuario;
import com.example.musicupc.repositories.ReseñaRepository;
import com.example.musicupc.repositories.ReservaRepository;
import com.example.musicupc.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReseñaService {

    private final ReseñaRepository reseñaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;

    public ReseñaService(
            ReseñaRepository reseñaRepository,
            UsuarioRepository usuarioRepository,
            ReservaRepository reservaRepository
    ) {
        this.reseñaRepository = reseñaRepository;
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<Reseña> listar() {
        return reseñaRepository.findAll();
    }

    public Reseña listarPorId(Long id) {
        return reseñaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));
    }

    public List<Reseña> listarPorUsuario(Long usuarioId) {
        return reseñaRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
    }

    public Double obtenerPromedioRatingPorReserva(Long reservaId) {
        Double promedio = reseñaRepository.calcularPromedioRatingPorReserva(reservaId);
        return promedio != null ? promedio : 0.0;
    }

    public Reseña registrar(Reseña reseña) {
        validarDatosBasicos(reseña);

        Usuario usuario = obtenerUsuario(reseña);
        Reserva reserva = obtenerReserva(reseña);

        validarReservaPagada(reserva);
        validarReservaPerteneceAlUsuario(reserva, usuario);

        if (reseñaRepository.existsByReserva_Id(reserva.getId())) {
            throw new RuntimeException("La reserva ya tiene una reseña registrada");
        }

        reseña.setUsuario(usuario);
        reseña.setReserva(reserva);

        return reseñaRepository.save(reseña);
    }

    public Reseña actualizar(Long id, Reseña reseñaDetalles) {
        Reseña reseñaExistente = listarPorId(id);

        validarDatosBasicos(reseñaDetalles);

        Usuario usuario = obtenerUsuario(reseñaDetalles);
        Reserva reserva = obtenerReserva(reseñaDetalles);

        validarReservaPagada(reserva);
        validarReservaPerteneceAlUsuario(reserva, usuario);

        if (reseñaRepository.existsByReserva_IdAndIdNot(reserva.getId(), id)) {
            throw new RuntimeException("La reserva ya tiene una reseña registrada");
        }

        reseñaExistente.setUsuario(usuario);
        reseñaExistente.setReserva(reserva);
        reseñaExistente.setRating(reseñaDetalles.getRating());
        reseñaExistente.setComentario(reseñaDetalles.getComentario());

        return reseñaRepository.save(reseñaExistente);
    }

    public void eliminar(Long id) {
        Reseña reseña = listarPorId(id);
        reseñaRepository.delete(reseña);
    }

    private void validarDatosBasicos(Reseña reseña) {
        if (reseña.getUsuario() == null || reseña.getUsuario().getId() == null) {
            throw new RuntimeException("El usuario es obligatorio");
        }

        if (reseña.getReserva() == null || reseña.getReserva().getId() == null) {
            throw new RuntimeException("La reserva es obligatoria");
        }

        if (reseña.getRating() == null || reseña.getRating() < 1 || reseña.getRating() > 5) {
            throw new RuntimeException("El rating debe estar entre 1 y 5");
        }

        if (reseña.getComentario() == null || reseña.getComentario().trim().isBlank()) {
            throw new RuntimeException("El comentario es obligatorio");
        }
    }

    private Usuario obtenerUsuario(Reseña reseña) {
        return usuarioRepository.findById(reseña.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private Reserva obtenerReserva(Reseña reseña) {
        return reservaRepository.findById(reseña.getReserva().getId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }

    private void validarReservaPagada(Reserva reserva) {
        if (reserva.getEstado() == null || !reserva.getEstado().equalsIgnoreCase("PAGADO")) {
            throw new RuntimeException("Solo se puede registrar una reseña para reservas pagadas");
        }
    }

    private void validarReservaPerteneceAlUsuario(Reserva reserva, Usuario usuario) {
        if (reserva.getCliente() == null || reserva.getCliente().getId() == null) {
            throw new RuntimeException("La reserva no tiene cliente asignado");
        }

        if (!reserva.getCliente().getId().equals(usuario.getId())) {
            throw new RuntimeException("La reserva no pertenece al usuario seleccionado");
        }
    }
}