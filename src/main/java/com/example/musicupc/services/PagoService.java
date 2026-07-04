package com.example.musicupc.services;

import com.example.musicupc.entities.Pago;
import com.example.musicupc.entities.Reserva;
import com.example.musicupc.repositories.PagoRepository;
import com.example.musicupc.repositories.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;
    private final TerminoService terminoService;

    public PagoService(
            PagoRepository pagoRepository,
            ReservaRepository reservaRepository,
            TerminoService terminoService
    ) {
        this.pagoRepository = pagoRepository;
        this.reservaRepository = reservaRepository;
        this.terminoService = terminoService;
    }

    public List<Pago> listar() {
        return pagoRepository.findAll();
    }

    public Pago listarPorId(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
    }

    @Transactional
    public Pago registrar(Pago pago) {
        validarDatosBasicos(pago);

        String metodo = normalizarMetodoPago(pago.getMetodo());
        Reserva reserva = obtenerReserva(pago);

        if (pagoRepository.existsByReserva_Id(reserva.getId())) {
            throw new RuntimeException("La reserva ya tiene un pago registrado");
        }

        validarReservaPendiente(reserva);
        validarMontoMinimo(pago, reserva);

        pago.setMetodo(metodo);
        pago.setEstado("COMPLETADO");
        pago.setFechaPago(LocalDateTime.now());
        pago.setReserva(reserva);

        reserva.setEstado("PAGADO");
        reservaRepository.save(reserva);

        terminoService.confirmarPorReserva(reserva.getId());

        return pagoRepository.save(pago);
    }

    @Transactional
    public Pago actualizar(Long id, Pago pagoDetalles) {
        Pago pagoExistente = listarPorId(id);

        validarDatosBasicos(pagoDetalles);

        String metodo = normalizarMetodoPago(pagoDetalles.getMetodo());
        Reserva reserva = obtenerReserva(pagoDetalles);

        if (pagoRepository.existsByReserva_IdAndIdNot(reserva.getId(), id)) {
            throw new RuntimeException("La reserva ya tiene un pago registrado");
        }

        if (pagoExistente.getReserva() != null
                && pagoExistente.getReserva().getId() != null
                && !pagoExistente.getReserva().getId().equals(reserva.getId())) {
            throw new RuntimeException("No se puede cambiar la reserva de un pago existente");
        }

        validarMontoMinimo(pagoDetalles, reserva);

        pagoExistente.setReserva(reserva);
        pagoExistente.setMonto(pagoDetalles.getMonto());
        pagoExistente.setMetodo(metodo);
        pagoExistente.setEstado("COMPLETADO");
        pagoExistente.setReferenciaTransaccion(pagoDetalles.getReferenciaTransaccion());

        if (pagoExistente.getFechaPago() == null) {
            pagoExistente.setFechaPago(LocalDateTime.now());
        }

        return pagoRepository.save(pagoExistente);
    }

    public void eliminar(Long id) {
        Pago pago = listarPorId(id);
        pagoRepository.delete(pago);
    }

    private void validarDatosBasicos(Pago pago) {
        if (pago.getReserva() == null || pago.getReserva().getId() == null) {
            throw new RuntimeException("La reserva es obligatoria para registrar el pago");
        }

        if (pago.getMonto() == null) {
            throw new RuntimeException("El monto es obligatorio");
        }
    }

    private Reserva obtenerReserva(Pago pago) {
        return reservaRepository.findById(pago.getReserva().getId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }

    private void validarReservaPendiente(Reserva reserva) {
        if (reserva.getEstado() == null) {
            throw new RuntimeException("La reserva no tiene estado");
        }

        String estado = reserva.getEstado().trim().toUpperCase();

        if (estado.equals("PAGADO") || estado.equals("COMPLETADO")) {
            throw new RuntimeException("La reserva ya se encuentra pagada");
        }

        if (!estado.equals("PENDING") && !estado.equals("PENDIENTE")) {
            throw new RuntimeException("Solo se pueden pagar reservas pendientes");
        }
    }

    private void validarMontoMinimo(Pago pago, Reserva reserva) {
        BigDecimal totalReserva = reserva.getPrecioTotal();

        if (totalReserva == null) {
            throw new RuntimeException("La reserva no tiene precio total");
        }

        if (pago.getMonto().compareTo(totalReserva) < 0) {
            throw new RuntimeException("El monto no puede ser menor al total de la reserva");
        }
    }

    private String normalizarMetodoPago(String metodo) {
        if (metodo == null || metodo.isBlank()) {
            throw new RuntimeException("El método de pago es obligatorio");
        }

        String metodoNormalizado = metodo.trim().toUpperCase();

        if (!metodoNormalizado.equals("TARJETA")
                && !metodoNormalizado.equals("YAPE")
                && !metodoNormalizado.equals("PLIN")) {
            throw new RuntimeException("Método de pago inválido. Solo se permite TARJETA, YAPE o PLIN");
        }

        return metodoNormalizado;
    }
}