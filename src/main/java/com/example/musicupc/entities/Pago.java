package com.example.musicupc.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Reserva reserva;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(name = "method", length = 50)
    private String metodo;

    @Column(name = "status", length = 30)
    private String estado;

    @Column(name = "transaction_ref", length = 100)
    private String referenciaTransaccion;

    @Column(name = "paid_at")
    private LocalDateTime fechaPago;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime fechaCreacion;
}