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
public class SuscripcionDTOInsert {

    private Long id;
    private Long usuario_id;
    private String tipo_plan;
    private double precio;
    private LocalDate fecha_inicio;
    private LocalDate fecha_fin;
    private String estado;
    private LocalDate fecha_creacion;

}
