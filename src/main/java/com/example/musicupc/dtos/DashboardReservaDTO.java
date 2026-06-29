package com.example.musicupc.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class DashboardReservaDTO {
    private Long id;
    private String cliente;
    private LocalDate fechaEvento;
    private LocalTime horaEvento;
    private String estado;
    private BigDecimal precioTotal;
}