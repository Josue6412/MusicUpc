package com.example.musicupc.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class DashboardPagoDTO {
    private Long id;
    private String concepto;
    private String tipo;
    private String metodo;
    private String estado;
    private BigDecimal monto;
    private LocalDateTime fechaPago;
}
