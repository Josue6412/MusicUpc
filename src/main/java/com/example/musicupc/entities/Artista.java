package com.example.musicupc.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Artista")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Artista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 40)
    private String nombreArtistico;
    @Column(nullable = false, length = 500)
    private String bio;
    @ManyToMany
    @JoinTable(
            name = "artista_genero",
            joinColumns = @JoinColumn(name = "artista_id"),
            inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    private List<Genero> generos = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;
    @Column(nullable = false)
    private boolean disponibilidad;
    @Column(nullable = false)
    private BigDecimal precioBase;
    @Column(nullable = false)
    private LocalDate fechaInicioCarrera;
    @Transient
    public int getAniosExperiencia() {
        return Period.between(fechaInicioCarrera, LocalDate.now()).getYears();
    }
}
