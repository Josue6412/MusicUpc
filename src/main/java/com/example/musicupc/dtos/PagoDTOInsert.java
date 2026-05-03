package com.example.musicupc.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PagoDTOInsert {
    private Long reservaId;
    private BigDecimal monto;
    private String metodo;
    private String estado;
    private String referenciaTransaccion;
}