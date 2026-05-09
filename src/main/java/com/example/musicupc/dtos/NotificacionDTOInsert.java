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
public class NotificacionDTOInsert {

    private Long id;
    private Long usuario_id;
    private String titulo;
    private String mensaje;
    private String tipo;
    private boolean leido;
    private LocalDate fecha_creacion;


}
