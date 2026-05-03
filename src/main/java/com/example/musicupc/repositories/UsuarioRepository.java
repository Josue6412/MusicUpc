package com.example.musicupc.repositories;

import com.example.musicupc.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByEmail(String email);

    boolean existsByDni(String dni);

    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
}
