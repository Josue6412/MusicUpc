package com.example.musicupc.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UsuarioDTOInsert {
    private String nombre;
    private String apellido;
    private String email;
    private String contrasena;
    private String telefono;
    private String dni;
    private String rol;
    private LocalDate fechaNacimiento;
    private String fotoPerfil;
}
