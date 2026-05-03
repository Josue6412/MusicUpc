package com.example.musicupc.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "bookings")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Usuario cliente;

    @Column(name = "event_date", nullable = false)
    private LocalDate fechaEvento;

    @Column(name = "event_time", nullable = false)
    private LocalTime horaEvento;

    @Column(name = "event_location", length = 300, nullable = false)
    private String ubicacionEvento;

    @Column(name = "event_type", length = 100)
    private String tipoEvento;

    @Column(name = "duration_hours")
    private Integer duracionHoras;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal precioTotal;

    @Column(name = "status", length = 30, nullable = false)
    private String estado = "PENDING";

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notas;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime fechaActualizacion;
}