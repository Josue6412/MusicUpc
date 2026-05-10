package com.example.musicupc.controllers;

import com.example.musicupc.dtos.ArtistaDTOInsert;
import com.example.musicupc.dtos.ArtistaDTOList;
import com.example.musicupc.entities.Artista;
import com.example.musicupc.services.ArtistaService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/artistas")
public class ArtistaController {

    private final ArtistaService artistaService;
    public ArtistaController(ArtistaService artistaService) {
        this.artistaService = artistaService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<ArtistaDTOList> listar(){
        return artistaService.listar().stream().map(this::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ArtistaDTOList listarPorId(@PathVariable Long id){
        return convertToDTO(artistaService.listarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public ArtistaDTOList registrar(@RequestBody ArtistaDTOInsert dto) {
        Artista artista = convertToEntity(dto);

        return convertToDTO(
                artistaService.registrar(artista, dto.getGenerosIds(), dto.getRegionId())
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ArtistaDTOList actualizar(@PathVariable Long id, @RequestBody ArtistaDTOInsert dto) {
        Artista artista = convertToEntity(dto);

        return convertToDTO(
                artistaService.actualizar(id, artista, dto.getGenerosIds(), dto.getRegionId())
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        artistaService.eliminar(id);
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<ArtistaDTOList> buscar(@RequestParam String nombre) {
        return artistaService.buscarPorNombre(nombre).stream().map(this::convertToDTO).toList();
    }

    @GetMapping("/disponibles")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<ArtistaDTOList> buscarDisponibles() {
        return artistaService.buscarDisponibles().stream().map(this::convertToDTO).toList();
    }

    private ArtistaDTOList convertToDTO(Artista artista) {
        return new ArtistaDTOList(
                artista.getId(),
                artista.getNombreArtistico(),
                artista.getBio(),
                artista.getGeneros() != null ? artista.getGeneros().stream().map(g -> g.getNombre()).toList() : List.of(),
                artista.getRegion() != null  ? artista.getRegion().getNombre() : null,
                artista.isDisponibilidad(),
                artista.getPrecioBase(),
                artista.getAniosExperiencia()
        );
    }

    private Artista convertToEntity(ArtistaDTOInsert dto) {
        Artista artista = new Artista();
        artista.setNombreArtistico(dto.getNombreArtistico());
        artista.setBio(dto.getBio());
        artista.setDisponibilidad(dto.isDisponible());
        artista.setPrecioBase(dto.getPrecioBase());
        artista.setFechaInicioCarrera(dto.getFechaInicioCarrera());
        return artista;
    }
}
