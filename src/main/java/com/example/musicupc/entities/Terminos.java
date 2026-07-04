package com.example.musicupc.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "terminos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Terminos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reservaId;

    @Column(name = "terminos", columnDefinition = "TEXT", nullable = false)
    private String terminos;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "fecha_confirmacion", length = 30)
    private LocalDate fecha_confirmacion;

    @Column(name = "fecha_creacion", length = 100)
    private LocalDate fecha_creacion;

}
