package com.example.musicupc.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datos para recuperar la contraseña sin servidor de correo:
 * se valida la identidad con email + DNI y se establece una nueva contraseña.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RecuperarPasswordDTO {
    private String email;
    private String dni;
    private String nuevaContrasena;
}
