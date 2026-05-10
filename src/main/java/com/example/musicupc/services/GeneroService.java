package com.example.musicupc.services;

import com.example.musicupc.dtos.GeneroArtistasDTO;
import com.example.musicupc.entities.Artista;
import com.example.musicupc.entities.Genero;
import com.example.musicupc.repositories.ArtistaRepository;
import com.example.musicupc.repositories.GeneroRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class GeneroService {
    private final GeneroRepository generoRepo;
    private final ArtistaRepository artistaRepo;

    public GeneroService(GeneroRepository generoRepo, ArtistaRepository artistaRepo) {
        this.generoRepo = generoRepo;
        this.artistaRepo = artistaRepo;
    }

    public List<Genero> listar(){
        return generoRepo.findAll();
    }

    public Genero listarPorId(Long id){
        return generoRepo.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el género con el id: " + id));
    }

    public Genero registrar(Genero genero){
        if (genero.getNombre() == null || genero.getNombre().isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del genero es obligatorio.");
        }
        if (generoRepo.existsByNombreIgnoreCase(genero.getNombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El género ya existe.");
        }
        return generoRepo.save(genero);
    }

    public Genero actualizar(Long id, Genero genero){
        Genero existente = listarPorId(id);

        if (genero.getNombre() == null || genero.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del género es obligatorio.");
        }
        if (generoRepo.existsByNombreIgnoreCase(genero.getNombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El género ya existe.");
        }

        existente.setNombre(genero.getNombre());
        return generoRepo.save(existente);
    }

    public void eliminar(Long id){
        Genero genero = listarPorId(id);

        List<Artista> artistas = artistaRepo.findAll();

        for (Artista a : artistas) {
            a.getGeneros().removeIf(g -> g.getId().equals(id));
        }

        artistaRepo.saveAll(artistas);

        generoRepo.delete(genero);
    }

    public List<Genero> buscarPorNombre(String nombre){
        return generoRepo.findByNombreContainingIgnoreCase(nombre);
    }

    public List<GeneroArtistasDTO> contarArtistasPorGenero() {

        List<GeneroArtistasDTO> resultados = generoRepo.contarArtistasPorGenero();

        if (resultados.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existen géneros registrados.");
        }

        return resultados;
    }
}
