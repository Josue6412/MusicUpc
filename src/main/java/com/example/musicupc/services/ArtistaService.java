package com.example.musicupc.services;

import com.example.musicupc.entities.Artista;
import com.example.musicupc.entities.Genero;
import com.example.musicupc.entities.Region;
import com.example.musicupc.repositories.ArtistaRepository;
import com.example.musicupc.repositories.GeneroRepository;
import com.example.musicupc.repositories.RegionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ArtistaService {
    private final ArtistaRepository artistaRepo;
    private final GeneroRepository generoRepo;
    private final RegionRepository regionRepo;

    public ArtistaService(ArtistaRepository artistaRepo, GeneroRepository generoRepo, RegionRepository regionRepo) {
        this.artistaRepo = artistaRepo;
        this.generoRepo = generoRepo;
        this.regionRepo = regionRepo;
    }

    public List<Artista> listar() {
        return artistaRepo.findAll();
    }

    public Artista listarPorId(Long id) {
        return artistaRepo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el artista con id: " + id));
    }

    public Artista registrar(Artista artista, List<Long> generosIds, Long regionId) {
        if (artista.getBio().split("\\s+").length > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La bio no puede tener más de 100 palabras.");
        }

        Region region = regionRepo.findById(regionId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Región no encontrada"));
        artista.setRegion(region);

        List<Genero> generos = generoRepo.findAllById(generosIds);
        if (generos.size() != generosIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Uno o más géneros no existen."
            );
        }
        artista.setGeneros(generos);

        return artistaRepo.save(artista);
    }

    public Artista actualizar(Long id, Artista artista, List<Long> generosIds, Long regionId) {
        Artista existente = listarPorId(id);

        existente.setNombreArtistico(artista.getNombreArtistico());
        existente.setBio(artista.getBio());
        existente.setDisponibilidad(artista.isDisponibilidad());
        existente.setPrecioBase(artista.getPrecioBase());
        existente.setFechaInicioCarrera(artista.getFechaInicioCarrera());

        Region region = regionRepo.findById(regionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Región no encontrada"));
        existente.setRegion(region);

        List<Genero> generos = generoRepo.findAllById(generosIds);
        existente.setGeneros(generos);

        return artistaRepo.save(existente);
    }

    public void eliminar(Long id) {
        Artista artista = listarPorId(id);
        artistaRepo.delete(artista);
    }

    public List<Artista> buscarPorNombre(String nombre) {
        return artistaRepo.findByNombreArtisticoContainingIgnoreCase(nombre);
    }

    public List<Artista> buscarDisponibles() {

        List<Artista> artistas = artistaRepo.findByDisponibilidadTrue();

        if (artistas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay artistas disponibles.");
        }

        return artistas;
    }
}
