package com.example.musicupc.repositories;

import com.example.musicupc.dtos.GeneroArtistasDTO;
import com.example.musicupc.entities.Genero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GeneroRepository extends JpaRepository<Genero, Long> {
    List<Genero> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    @Query("SELECT new com.example.musicupc.dtos.GeneroArtistasDTO(\n" +
            " g.nombre,\n" +
            " COUNT(a)\n" +
            " )\n" +
            " FROM Genero g\n" +
            " LEFT JOIN g.artistas a\n" +
            " GROUP BY g.nombre\n" +
            " ORDER BY COUNT(a) DESC")
    List<GeneroArtistasDTO> contarArtistasPorGenero();
}
