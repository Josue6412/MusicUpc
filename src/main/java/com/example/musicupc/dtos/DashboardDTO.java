package com.example.musicupc.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private long totalUsuarios;
    private long totalArtistas;
    private long reservasTotales;
    private long reservasPagadas;
    private long reservasPendientes;
    private long pagosReservas;
    private long pagosSuscripciones;
    private long suscripcionesPagadas;
    private long suscripcionesPendientes;
    private BigDecimal ingresosReservas;
    private BigDecimal ingresosSuscripciones;
    private BigDecimal ingresosTotales;
    private List<DashboardReservaDTO> ultimasReservas;
}