package com.example.musicupc.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ReseñaDTOList {
    private Long id;
    private Long usuarioId;
    private String nombreUsuario;
    private Long reservaId;
    private String reservaDetalle;
    private String artistaNombre;
    private Integer rating;
    private String comentario;
    private LocalDateTime fechaCreacion;
}