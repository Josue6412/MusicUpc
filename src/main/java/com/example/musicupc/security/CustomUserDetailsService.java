package com.example.musicupc.security;

import com.example.musicupc.entities.Usuario;
import com.example.musicupc.repositories.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepo;

    public CustomUserDetailsService(
            UsuarioRepository usuarioRepo
    ) {

        this.usuarioRepo = usuarioRepo;
    }

    @Override
    public UserDetails loadUserByUsername(
            String username
    ) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new User(
                usuario.getEmail(),
                usuario.getContrasena(),
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + usuario.getRol()
                        )
                )
        );
    }
}
