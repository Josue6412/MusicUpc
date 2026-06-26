package com.example.musicupc.controllers;

import com.example.musicupc.dtos.*;
import com.example.musicupc.entities.Usuario;
import com.example.musicupc.repositories.UsuarioRepository;
import com.example.musicupc.security.JwtUtil;
import com.example.musicupc.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthController {
    private final AuthenticationManager authManager;

    private final JwtUtil jwtUtil;

    private final UsuarioRepository usuarioRepo;

    private final UsuarioService usuarioService;

    public AuthController(
            AuthenticationManager authManager,
            JwtUtil jwtUtil,
            UsuarioRepository usuarioRepo,
            UsuarioService usuarioService
    ) {

        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.usuarioRepo = usuarioRepo;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(
            @RequestBody LoginRequestDTO request
    ) {

        authManager.authenticate(

                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // El usuario se autentica con su email. Cargamos sus datos para
        // incluir rol e id dentro del token.
        Usuario usuario = usuarioRepo
                .findByEmail(request.getUsername())
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado")
                );

        return new LoginResponseDTO(tokenDe(usuario));
    }

    /**
     * Registro público. La cuenta se crea SIEMPRE con rol USUARIO y, si todo
     * va bien, se devuelve un token para que quede logueado al instante.
     */
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponseDTO registro(@RequestBody UsuarioDTOInsert dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setContrasena(dto.getContrasena());
        usuario.setTelefono(dto.getTelefono());
        usuario.setDni(dto.getDni());
        usuario.setFechaNacimiento(dto.getFechaNacimiento());

        Usuario creado = usuarioService.registrarComoUsuario(usuario);
        return new LoginResponseDTO(tokenDe(creado));
    }

    /** Recuperar contraseña validando email + DNI (sin servidor de correo). */
    @PostMapping("/recuperar")
    public Map<String, String> recuperar(@RequestBody RecuperarPasswordDTO dto) {
        usuarioService.recuperarContrasena(
                dto.getEmail(), dto.getDni(), dto.getNuevaContrasena());
        return Map.of("message", "Contraseña actualizada correctamente.");
    }

    private String tokenDe(Usuario usuario) {
        return jwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getId(),
                usuario.getNombre()
        );
    }
}
