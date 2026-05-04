package com.example.musicupc.dtos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PagoDTOList {
    private Long id;
    private Long reservaId;
    private BigDecimal monto;
    private String metodo;
    private String estado;
    private String referenciaTransaccion;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaCreacion;
}