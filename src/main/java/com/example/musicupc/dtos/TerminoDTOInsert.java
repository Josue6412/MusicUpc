package com.example.musicupc.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TerminoDTOInsert {
    private Long reservaId;
    private String terminos;
    private String status;
    private LocalDate fecha_confirmacion;
    private LocalDate fecha_creacion;
}
