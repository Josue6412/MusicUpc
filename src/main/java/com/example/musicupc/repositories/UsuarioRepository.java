package com.example.musicupc.repositories;

import com.example.musicupc.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByEmail(String email);

    boolean existsByDni(String dni);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    List<Usuario> findByRolContainingIgnoreCase(String rol);
}
