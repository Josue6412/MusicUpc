package com.example.musicupc.repositories;

import com.example.musicupc.entities.Genero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneroRepository extends JpaRepository<Genero, Long> {
    List<Genero> findByNombreContainingIgnoreCase(String nombre);
}
