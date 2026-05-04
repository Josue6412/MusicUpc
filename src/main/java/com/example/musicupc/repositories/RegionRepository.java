package com.example.musicupc.repositories;

import com.example.musicupc.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findByNombreContainingIgnoreCase(String nombre);
}
