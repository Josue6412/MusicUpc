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
    @Column(name = "plan_type", nullable = false)
    private String plan_type;
    @Column(name = "price", nullable = false)
    private double price;
    @Column(name = "start_date", nullable = false)
    private LocalDate start_date;
    @Column(name = "end_date", nullable = false)
    private LocalDate end_date;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "timestamp", nullable = false)
    private LocalDate timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Usuario usuario;





}
