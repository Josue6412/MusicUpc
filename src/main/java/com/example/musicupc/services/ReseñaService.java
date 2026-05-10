package com.example.musicupc.services;

import com.example.musicupc.entities.Reseña;
import com.example.musicupc.repositories.ReseñaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReseñaService {

    private final ReseñaRepository reseñaRepository;

    public ReseñaService(ReseñaRepository reseñaRepository) {
        this.reseñaRepository = reseñaRepository;
    }

    public List<Reseña> listar() {
        return reseñaRepository.findAll();
    }

    public Reseña listarPorId(Long id) {
        return reseñaRepository.findById(id).orElseThrow(() -> new RuntimeException("Reseña no encontrada"));
    }

    public Reseña registrar(Reseña reseña) {
        if (reseña.getRating() == null || reseña.getRating() < 1 || reseña.getRating() > 5) {
            throw new RuntimeException("El rating debe estar entre 1 y 5");
        }
        return reseñaRepository.save(reseña);
    }

    public Reseña actualizar(Long id, Reseña reseñaDetalles) {
        Reseña reseñaExistente = listarPorId(id);

        if (reseñaDetalles.getRating() == null || reseñaDetalles.getRating() < 1 || reseñaDetalles.getRating() > 5) {
            throw new RuntimeException("El rating debe estar entre 1 y 5");
        }

        reseñaExistente.setUsuario(reseñaDetalles.getUsuario());
        reseñaExistente.setReserva(reseñaDetalles.getReserva());
        reseñaExistente.setRating(reseñaDetalles.getRating());
        reseñaExistente.setComentario(reseñaDetalles.getComentario());

        return reseñaRepository.save(reseñaExistente);
    }

    public void eliminar(Long id) {
        reseñaRepository.deleteById(id);
    }
}