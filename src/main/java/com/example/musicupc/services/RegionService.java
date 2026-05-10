package com.example.musicupc.services;

import com.example.musicupc.dtos.RegionArtistasDTO;
import com.example.musicupc.entities.Region;
import com.example.musicupc.repositories.ArtistaRepository;
import com.example.musicupc.repositories.RegionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"No existe la región con id: " + id));
    }

    public Region insertar(Region region) {
        if (region.getNombre() == null || region.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El nombre es obligatorio");
        }
        if (region.getDepartamento() == null || region.getDepartamento().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El departamento es obligatorio.");
        }
        if (regionRepo.existsByNombreIgnoreCaseAndDepartamentoIgnoreCase(region.getNombre(), region.getDepartamento())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La región ya existe.");
        }
        return regionRepo.save(region);
    }

    public Region actualizar(Long id, Region region) {
        Region existente =  listarPorId(id);

        if (region.getNombre() == null || region.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio.");
        }

        if (region.getDepartamento() == null || region.getDepartamento().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El departamento es obligatorio.");
        }
        boolean existeDuplicado = regionRepo.existsByNombreIgnoreCaseAndDepartamentoIgnoreCase(
                        region.getNombre(),
                        region.getDepartamento()
                );

        boolean mismoRegistro =
                existente.getNombre().equalsIgnoreCase(region.getNombre()) &&
                        existente.getDepartamento().equalsIgnoreCase(region.getDepartamento());

        if (existeDuplicado && !mismoRegistro) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La región ya existe."
            );
        }

        existente.setNombre(region.getNombre());
        existente.setDepartamento(region.getDepartamento());

        return regionRepo.save(existente);
    }

    public void eliminar(Long id) {
        if (artistaRepo.existsByRegionId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"No se puede eliminar la región porque tiene artistas asociados");
        }

        Region region = listarPorId(id);
        regionRepo.delete(region);
    }

    public List<Region> buscarPorNombre(String nombre) {
        return regionRepo.findByNombreContainingIgnoreCase(nombre);
    }

    public List<RegionArtistasDTO> contarArtistasPorRegion() {

        List<RegionArtistasDTO> resultados = regionRepo.contarArtistasPorRegion();

        if (resultados.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existen regiones registradas.");
        }

        return resultados;
    }
}
