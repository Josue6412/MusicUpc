package com.example.musicupc.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "Suscripcion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Suscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tipo_plan", nullable = false)
    private String tipo_plan;
    @Column(name = "precio", nullable = false)
    private double precio;
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fecha_inicio;
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fecha_fin;
    @Column(name = "estado", nullable = false)
    private String estado;
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fecha_creacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
