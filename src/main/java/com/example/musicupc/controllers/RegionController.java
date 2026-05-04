package com.example.musicupc.controllers;

import com.example.musicupc.dtos.RegionDTOInsert;
import com.example.musicupc.dtos.RegionDTOList;
import com.example.musicupc.entities.Region;
import com.example.musicupc.services.RegionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/regiones")
public class RegionController {
    private final RegionService regionService;
    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping
    public List<RegionDTOList> listar() {
        return regionService.listar().stream().map(this::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    public RegionDTOList listarPorId(@PathVariable Long id){
        return convertToDTO(regionService.listarPorId(id));
    }

    @PostMapping
    public RegionDTOList registrar(@RequestBody RegionDTOInsert dto) {
        return convertToDTO(
                regionService.insertar(convertToEntity(dto))
        );
    }

    @PutMapping("/{id}")
    public RegionDTOList actualizar(@PathVariable Long id, @RequestBody RegionDTOInsert dto) {
        return convertToDTO(
                regionService.actualizar(id, convertToEntity(dto))
        );
    }

    @GetMapping("/buscar")
    public List<RegionDTOList> buscar(@RequestParam String nombre) {
        return regionService.buscarPorNombre(nombre)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    private RegionDTOList convertToDTO(Region region) {
        return new RegionDTOList(
                region.getId(),
                region.getNombre(),
                region.getDepartamento()
        );
    }

    private Region convertToEntity(RegionDTOInsert dto) {
        Region region = new Region();
        region.setNombre(dto.getNombre());
        region.setDepartamento(dto.getDepartamento());
        return region;
    }
}
