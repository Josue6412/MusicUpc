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
public class ReservaUsuarioDTO {
    private String nombre;
    private Long reservaId;
    private String status;
    private String notes;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private BigDecimal totalPrice;
}
