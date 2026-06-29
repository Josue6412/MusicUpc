package com.example.musicupc.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PagoSuscripcionDTOList {
    private Long id;
    private Long suscripcionId;
    private Long usuarioId;
    private BigDecimal monto;
    private String tipoPlan;
    private String metodo;
    private String estado;
    private String referenciaTransaccion;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaCreacion;
}