package com.example.musicupc.controllers;

import com.example.musicupc.dtos.GeneroDTOInsert;
import com.example.musicupc.dtos.GeneroDTOList;
import com.example.musicupc.entities.Genero;
import com.example.musicupc.services.GeneroService;
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
    public List<GeneroDTOList> listar() {
        return generoService.listar().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public GeneroDTOList listarPorId(@PathVariable Long id){
        return convertToDTO(generoService.listarPorId(id));
    }

    @PostMapping
    public GeneroDTOList registrar(@RequestBody GeneroDTOInsert dto){
        Genero genero = convertToEntity(dto);
        return convertToDTO(generoService.registrar(genero));
    }

    @PutMapping("/{id}")
    public GeneroDTOList actualizar(@PathVariable Long id, @RequestBody GeneroDTOInsert dto){
        Genero genero = convertToEntity(dto);
        return convertToDTO(generoService.actualizar(id, genero));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id){
        generoService.eliminar(id);
    }

    @GetMapping("/buscar")
    public List<GeneroDTOList> buscarPorNombre(@RequestParam String nombre){
        return generoService.buscarPorNombre(nombre).stream().map(this::convertToDTO).toList();
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
