package com.example.musicupc.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "Genero")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Genero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 40)
    private String nombre;
    @ManyToMany(mappedBy = "generos")
    @JsonIgnore
    private List<Artista> artistas;
}
