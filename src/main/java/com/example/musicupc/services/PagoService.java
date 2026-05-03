package com.example.musicupc.services;

import com.example.musicupc.entities.Pago;
import com.example.musicupc.repositories.PagoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;

    public PagoService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    public List<Pago> listar() {
        return pagoRepository.findAll();
    }

    public Pago listarPorId(Long id) {
        return pagoRepository.findById(id).orElseThrow(() -> new RuntimeException("Pago no encontrado"));
    }

    public Pago registrar(Pago pago) {
        if (pago.getEstado() != null && pago.getEstado().equalsIgnoreCase("succeeded")) {
            pago.setFechaPago(LocalDateTime.now());
        }
        return pagoRepository.save(pago);
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