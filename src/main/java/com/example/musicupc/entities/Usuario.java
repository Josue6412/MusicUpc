package com.example.musicupc.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "Usuario")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre", nullable = false)
    private String nombre;
    @Column(name = "apellido", nullable = false)
    private String apellido;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "contrasena", nullable = false)
    private String contrasena;
    @Column(name = "telefono", nullable = false)
    private String telefono;
    @Column(name = "dni",  nullable = false, unique = true)
    private String dni;
    @Column(name = "rol",  nullable = false)
    private String rol;
    @Column(name = "fechaNacimiento", nullable = false)
    private LocalDate fechaNacimiento;
    @CreationTimestamp
    @Column(name = "fechaRegistro", updatable = false)
    private LocalDate fechaRegistro;
}