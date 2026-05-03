package com.example.musicupc.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ReservaDTOList {
    private Long id;
    private Long clienteId;
    private LocalDate fechaEvento;
    private LocalTime horaEvento;
    private String ubicacionEvento;
    private String tipoEvento;
    private Integer duracionHoras;
    private BigDecimal precioTotal;
    private String estado;
    private String notas;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}