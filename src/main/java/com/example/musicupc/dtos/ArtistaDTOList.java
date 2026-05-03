package com.example.musicupc.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ArtistaDTOList {
    private Long id;
    private String nombreArtistico;
    private String bio;
    private List<String> generos;
    private String region;
    private boolean disponible;
    private BigDecimal precioBase;
    private int aniosExperiencia;
}
