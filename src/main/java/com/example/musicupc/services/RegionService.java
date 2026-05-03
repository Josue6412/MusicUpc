package com.example.musicupc.services;

import com.example.musicupc.entities.Region;
import com.example.musicupc.repositories.ArtistaRepository;
import com.example.musicupc.repositories.RegionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionService {
    private final RegionRepository regionRepo;
    private final ArtistaRepository artistaRepo;

    public RegionService(RegionRepository regionRepo, ArtistaRepository artistaRepo) {
        this.regionRepo = regionRepo;
        this.artistaRepo = artistaRepo;
    }

    public List<Region>listar() {
        return regionRepo.findAll();
    }

    public Region listarPorId(Long id) {
        return regionRepo.findById(id).
                orElseThrow(() -> new RuntimeException("No existe la región con id: " + id));
    }

    public Region insertar(Region region) {
        if (region.getNombre() == null || region.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        return regionRepo.save(region);
    }

    public Region actualizar(Long id, Region region) {
        Region existente =  listarPorId(id);

        existente.setNombre(region.getNombre());
        existente.setDepartamento(region.getDepartamento());

        return regionRepo.save(existente);
    }

    public void eliminar(Long id) {
        if (artistaRepo.existsByRegionId(id)) {
            throw new RuntimeException("No se puede eliminar la región porque tiene artistas asociados");
        }

        Region region = listarPorId(id);
        regionRepo.delete(region);
    }

    public List<Region> buscarPorNombre(String nombre) {
        return regionRepo.findByNombreContainingIgnoreCase(nombre);
    }
}
