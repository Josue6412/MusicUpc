package com.example.musicupc.services;

import com.example.musicupc.entities.Pago;
import com.example.musicupc.entities.Reserva;
import com.example.musicupc.repositories.PagoRepository;
import com.example.musicupc.repositories.ReservaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;

    public PagoService(PagoRepository pagoRepository,  ReservaRepository reservaRepository) {
        this.pagoRepository = pagoRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<Pago> listar() {
        return pagoRepository.findAll();
    }

    public Pago listarPorId(Long id) {
        return pagoRepository.findById(id).orElseThrow(() -> new RuntimeException("Pago no encontrado"));
    }

    public Pago registrar(Pago pago) {
        validarMetodoPago(pago.getMetodo());

        pago.setEstado("COMPLETADO");
        pago.setFechaPago(LocalDateTime.now());

        if (pago.getReserva() == null || pago.getReserva().getId() == null) {
            throw new RuntimeException("La reserva es obligatoria para registrar el pago");
        }

        Reserva reserva = reservaRepository.findById(pago.getReserva().getId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        reserva.setEstado("PAGADO");
        reservaRepository.save(reserva);

        pago.setReserva(reserva);

        return pagoRepository.save(pago);
    }

    private void validarMetodoPago(String metodo) {
        if (metodo == null || metodo.isBlank()) {
            throw new RuntimeException("El método de pago es obligatorio");
        }

        String metodoNormalizado = metodo.trim().toUpperCase();

        if (!metodoNormalizado.equals("TARJETA")
                && !metodoNormalizado.equals("YAPE")
                && !metodoNormalizado.equals("PLIN")) {
            throw new RuntimeException("Método de pago inválido. Solo se permite TARJETA, YAPE o PLIN");
        }
    }

    public Pago actualizar(Long id, Pago pagoDetalles) {
        Pago pagoExistente = listarPorId(id);

        pagoExistente.setReserva(pagoDetalles.getReserva());
        pagoExistente.setMonto(pagoDetalles.getMonto());
        pagoExistente.setMetodo(pagoDetalles.getMetodo());
        pagoExistente.setEstado(pagoDetalles.getEstado());
        pagoExistente.setReferenciaTransaccion(pagoDetalles.getReferenciaTransaccion());

        if (pagoDetalles.getEstado() != null && pagoDetalles.getEstado().equalsIgnoreCase("succeeded") && pagoExistente.getFechaPago() == null) {
            pagoExistente.setFechaPago(LocalDateTime.now());
        }

        return pagoRepository.save(pagoExistente);
    }

    public void eliminar(Long id) {
        pagoRepository.deleteById(id);
    }
}