package com.example.musicupc.controllers;

import com.example.musicupc.dtos.GeneroArtistasDTO;
import com.example.musicupc.dtos.GeneroDTOInsert;
import com.example.musicupc.dtos.GeneroDTOList;
import com.example.musicupc.entities.Genero;
import com.example.musicupc.services.GeneroService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/generos")
public class GeneroController {
    private final GeneroService generoService;
    public GeneroController(GeneroService generoService){
        this.generoService = generoService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<GeneroDTOList> listar() {
        return generoService.listar().stream().map(this::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public GeneroDTOList listarPorId(@PathVariable Long id){
        return convertToDTO(generoService.listarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public GeneroDTOList registrar(@RequestBody GeneroDTOInsert dto){
        Genero genero = convertToEntity(dto);
        return convertToDTO(generoService.registrar(genero));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public GeneroDTOList actualizar(@PathVariable Long id, @RequestBody GeneroDTOInsert dto){
        Genero genero = convertToEntity(dto);
        return convertToDTO(generoService.actualizar(id, genero));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id){
        generoService.eliminar(id);
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<GeneroDTOList> buscarPorNombre(@RequestParam String nombre){
        return generoService.buscarPorNombre(nombre).stream().map(this::convertToDTO).toList();
    }

    @GetMapping("/estadisticas/artistas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<GeneroArtistasDTO> contarArtistasPorGenero() {

        return generoService.contarArtistasPorGenero();
    }

    private GeneroDTOList convertToDTO(Genero genero) {
        return new GeneroDTOList(
                genero.getId(),
                genero.getNombre()
        );
    }

    private Genero convertToEntity(GeneroDTOInsert dto) {
        Genero genero = new Genero();
        genero.setNombre(dto.getNombre());
        return genero;
    }
}
