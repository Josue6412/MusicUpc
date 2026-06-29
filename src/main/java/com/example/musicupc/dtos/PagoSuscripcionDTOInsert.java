package com.example.musicupc.dtos;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PagoSuscripcionDTOInsert {
    private Long suscripcionId;
    private BigDecimal monto;
    private String tipoPlan;
    private String metodo;
    private String referenciaTransaccion;
}
