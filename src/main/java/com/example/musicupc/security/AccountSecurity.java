package com.example.musicupc.security;

import com.example.musicupc.repositories.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Componente de seguridad usado desde @PreAuthorize para comprobar que el
 * usuario autenticado es el dueño del recurso que intenta consultar/editar.
 *
 * Uso: @PreAuthorize("hasRole('ADMINISTRADOR') or @accountSecurity.isSelf(#id, authentication)")
 */
@Component("accountSecurity")
public class AccountSecurity {

    private final UsuarioRepository usuarioRepo;

    public AccountSecurity(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    /** ¿El usuario autenticado (por email) tiene el id indicado? */
    public boolean isSelf(Long id, Authentication authentication) {
        if (id == null || authentication == null) {
            return false;
        }
        return usuarioRepo.findByEmail(authentication.getName())
                .map(u -> u.getId().equals(id))
                .orElse(false);
    }
}
