package com.example.musicupc.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ReseñaDTOInsert {
    private Long usuarioId;
    private Long reservaId;
    private Integer rating;
    private String comentario;
}