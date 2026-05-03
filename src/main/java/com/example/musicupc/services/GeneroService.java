package com.example.musicupc.services;

import com.example.musicupc.entities.Artista;
import com.example.musicupc.entities.Genero;
import com.example.musicupc.repositories.ArtistaRepository;
import com.example.musicupc.repositories.GeneroRepository;
import org.springframework.stereotype.Service;

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
                .orElseThrow(()-> new RuntimeException("No existe el género con el id: " + id));
    }

    public Genero registrar(Genero genero){
        if (genero.getNombre() == null || genero.getNombre().isBlank()){
            throw new IllegalArgumentException("El nombre del genero es obligatorio.");
        }
        return generoRepo.save(genero);
    }

    public Genero actualizar(Long id, Genero genero){
        Genero existente = listarPorId(id);

        existente.setNombre(genero.getNombre());
        return generoRepo.save(existente);
    }

    public void eliminar(Long id){
        Genero genero = listarPorId(id);

        List<Artista> artistas = artistaRepo.findAll();

        for (Artista a : artistas) {
            a.getGeneros().removeIf(g -> g.getId().equals(id));
        }

        generoRepo.delete(genero);
    }

    public List<Genero> buscarPorNombre(String nombre){
        return generoRepo.findByNombreContainingIgnoreCase(nombre);
    }
}
