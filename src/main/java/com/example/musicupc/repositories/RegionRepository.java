package com.example.musicupc.repositories;

import com.example.musicupc.dtos.RegionArtistasDTO;
import com.example.musicupc.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndDepartamentoIgnoreCase(String nombre, String departamento);

    @Query("SELECT new com.example.musicupc.dtos.RegionArtistasDTO(\n" +
            " r.nombre,\n" +
            " COUNT(a)\n" +
            " )\n" +
            " FROM Region r\n" +
            " LEFT JOIN r.artistas a\n" +
            " GROUP BY r.nombre\n" +
            " ORDER BY COUNT(a) DESC")
    List<RegionArtistasDTO> contarArtistasPorRegion();
}
