package com.example.musicupc.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET =
            "musicupcsecuritymusicupcsecurity";

    private final Key key =
            Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(String username) {

        return generateToken(username, null, null, null);
    }

    // Versión enriquecida: incluye el rol y el id del usuario como claims,
    // para que el frontend pueda leerlos del token (redirección por rol, etc.).
    public String generateToken(
            String username,
            String rol,
            Long id,
            String nombre
    ) {

        JwtBuilder builder = Jwts.builder()

                .setSubject(username)

                .setIssuedAt(new Date())

                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 1000 * 60 * 60
                        )
                );

        if (rol != null) builder.claim("rol", rol);
        if (id != null) builder.claim("id", id);
        if (nombre != null) builder.claim("nombre", nombre);

        return builder.signWith(key).compact();
    }

    public String extractUsername(String token) {

        return Jwts.parserBuilder()

                .setSigningKey(key)

                .build()

                .parseClaimsJws(token)

                .getBody()

                .getSubject();
    }
}
