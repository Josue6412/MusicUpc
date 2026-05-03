package com.example.musicupc.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RegionDTOList {
    private Long id;
    private String nombre;
    private String departamento;
}
