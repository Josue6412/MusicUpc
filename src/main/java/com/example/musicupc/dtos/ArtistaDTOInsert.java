package com.example.musicupc.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ArtistaDTOInsert {
    private String nombreArtistico;
    private String bio;
    private List<Long> generosIds;
    private Long regionId;
    private boolean disponible;
    private BigDecimal precioBase;
    private LocalDate fechaInicioCarrera;
}
