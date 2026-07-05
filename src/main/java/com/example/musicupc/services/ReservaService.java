package com.example.musicupc.services;

import com.example.musicupc.entities.Reserva;
import com.example.musicupc.entities.Usuario;
import com.example.musicupc.repositories.ReservaRepository;
import com.example.musicupc.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.musicupc.entities.Artista;
import com.example.musicupc.repositories.ArtistaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArtistaRepository artistaRepository;
    private final TerminoService terminoService;

    public ReservaService(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository, ArtistaRepository artistaRepository, TerminoService terminoService) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.artistaRepository = artistaRepository;
        this.terminoService = terminoService;
    }

    public List<Reserva> listar() {
        return reservaRepository.findAll();
    }

    public Reserva listarPorId(Long id) {
        return reservaRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La reserva con id " + id + " no existe."
                )
        );
    }

    @Transactional
    public Reserva registrar(Reserva reserva) {
        validarReserva(reserva);

        Usuario cliente = obtenerCliente(reserva);
        Artista artista = obtenerArtistaSiExiste(reserva);

        reserva.setCliente(cliente);
        reserva.setArtista(artista);
        reserva.setUbicacionEvento(reserva.getUbicacionEvento().trim());
        reserva.setTipoEvento(reserva.getTipoEvento().trim());
        reserva.setEstado("PENDING");

        Reserva reservaGuardada = reservaRepository.save(reserva);

        terminoService.crearAutomaticoParaReserva(reservaGuardada);

        return reservaGuardada;
    }

    @Transactional
    public Reserva actualizar(Long id, Reserva reservaDetalles) {
        Reserva reservaExistente = listarPorId(id);

        validarReserva(reservaDetalles);

        Usuario cliente = obtenerCliente(reservaDetalles);
        Artista artista = obtenerArtistaSiExiste(reservaDetalles);

        reservaExistente.setCliente(cliente);
        reservaExistente.setArtista(artista);
        reservaExistente.setFechaEvento(reservaDetalles.getFechaEvento());
        reservaExistente.setHoraEvento(reservaDetalles.getHoraEvento());
        reservaExistente.setUbicacionEvento(reservaDetalles.getUbicacionEvento().trim());
        reservaExistente.setTipoEvento(reservaDetalles.getTipoEvento().trim());
        reservaExistente.setDuracionHoras(reservaDetalles.getDuracionHoras());
        reservaExistente.setPrecioTotal(reservaDetalles.getPrecioTotal());
        reservaExistente.setNotas(reservaDetalles.getNotas());

        return reservaRepository.save(reservaExistente);
    }

    public void eliminar(Long id) {
        Reserva reserva = listarPorId(id);
        reservaRepository.delete(reserva);
    }

    public List<Reserva> buscarReservasPorUsuario(Long id) {
        return reservaRepository.buscarReservasPorUsuario(id);
    }

    private void validarReserva(Reserva reserva) {
        if (reserva.getCliente() == null || reserva.getCliente().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El cliente es obligatorio."
            );
        }

        if (reserva.getFechaEvento() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha del evento es obligatoria."
            );
        }

        if (reserva.getHoraEvento() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La hora del evento es obligatoria."
            );
        }

        LocalDateTime fechaHoraEvento = LocalDateTime.of(
                reserva.getFechaEvento(),
                reserva.getHoraEvento()
        );

        if (fechaHoraEvento.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La reserva debe realizarse con al menos 1 hora de anticipación."
            );
        }

        if (reserva.getUbicacionEvento() == null || reserva.getUbicacionEvento().trim().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La ubicación del evento es obligatoria."
            );
        }

        if (reserva.getUbicacionEvento().trim().length() > 300) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La ubicación no puede superar los 300 caracteres."
            );
        }

        if (reserva.getTipoEvento() == null || reserva.getTipoEvento().trim().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo de evento es obligatorio."
            );
        }

        if (reserva.getTipoEvento().trim().length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo de evento no puede superar los 100 caracteres."
            );
        }

        if (reserva.getDuracionHoras() == null || reserva.getDuracionHoras() < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La duración debe ser de al menos 1 hora."
            );
        }

        if (reserva.getPrecioTotal() == null || reserva.getPrecioTotal().compareTo(BigDecimal.ONE) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El precio total debe ser mayor o igual a S/ 1."
            );
        }
    }

    private Usuario obtenerCliente(Reserva reserva) {
        return usuarioRepository.findById(reserva.getCliente().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El cliente seleccionado no existe."
                ));
    }

    private Artista obtenerArtistaSiExiste(Reserva reserva) {
        if (reserva.getArtista() == null || reserva.getArtista().getId() == null) {
            return null;
        }

        Artista artista = artistaRepository.findById(reserva.getArtista().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El artista seleccionado no existe."
                ));

        if (!artista.isDisponibilidad()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El artista seleccionado no está disponible."
            );
        }

        return artista;
    }
}