package com.example.musicupc.services;

import com.example.musicupc.entities.Usuario;
import com.example.musicupc.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;
    public UsuarioService(UsuarioRepository usuarioRepo, PasswordEncoder passwordEncoder) {
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listar() {
        return usuarioRepo.findAll();
    }

    public Usuario listarPorId(Long id) {
        return usuarioRepo.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario con id: " + id + " no existe."));
    }

    public Usuario registrar(Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del usuario es obligatorio.");
        }
        if (usuario.getApellido() == null || usuario.getApellido().isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El apellido del usuario es obligatorio.");
        }
        if (usuario.getEmail() == null || !usuario.getEmail().contains("@")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email no es válido.");
        }
        if (usuarioRepo.existsByEmail(usuario.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        }
        if (usuario.getContrasena() == null || usuario.getContrasena().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria.");
        }
        usuario.setContrasena(
                passwordEncoder.encode(usuario.getContrasena())
        );
        if (usuario.getTelefono() == null || usuario.getTelefono().length() != 9){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El telefono debe tener 9 dígitos.");
        }
        if (usuario.getDni() == null || usuario.getDni().length() != 8){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El dni debe tener 8 dígitos.");
        }
        if (usuarioRepo.existsByDni(usuario.getDni())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El DNI ya está registrado");
        }
        if (usuario.getRol() == null || usuario.getRol().isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol del usuario es obligatorio.");
        }
        if(usuario.getFechaNacimiento() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de nacimiento del usuario es obligatoria.");
        }
        return usuarioRepo.save(usuario);
    }

    /** Registro público: la cuenta SIEMPRE se crea con rol USUARIO. */
    public Usuario registrarComoUsuario(Usuario usuario) {
        usuario.setRol("USUARIO");
        return registrar(usuario);
    }

    /** Recuperación sin correo: valida email + DNI y fija una nueva contraseña. */
    public void recuperarContrasena(String email, String dni, String nuevaContrasena) {
        if (email == null || email.isBlank() || dni == null || dni.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email y DNI son obligatorios.");
        }
        if (nuevaContrasena == null || nuevaContrasena.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La nueva contraseña es obligatoria.");
        }
        Usuario usuario = usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe una cuenta con ese email."));
        if (!dni.equals(usuario.getDni())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El DNI no coincide con la cuenta indicada.");
        }
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        usuarioRepo.save(usuario);
    }

    public Usuario actualizar(Long id, Usuario usuario, boolean puedeCambiarRol) {
        Usuario existente = listarPorId(id);

        existente.setNombre(usuario.getNombre());
        existente.setApellido(usuario.getApellido());
        existente.setEmail(usuario.getEmail());

        // Solo se cambia la contraseña si se envió una nueva (si va vacía,
        // se conserva la actual; antes se sobrescribía y bloqueaba la cuenta).
        if (usuario.getContrasena() != null && !usuario.getContrasena().isBlank()) {
            existente.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        }

        existente.setTelefono(usuario.getTelefono());
        existente.setDni(usuario.getDni());

        // Solo un administrador puede cambiar el rol (evita auto-ascensos).
        if (puedeCambiarRol && usuario.getRol() != null && !usuario.getRol().isBlank()) {
            existente.setRol(usuario.getRol());
        }

        existente.setFechaNacimiento(usuario.getFechaNacimiento());
        existente.setFotoPerfil(usuario.getFotoPerfil());

        return usuarioRepo.save(existente);
    }

    public void eliminar(Long id) {
        Usuario usuario = listarPorId(id);
        usuarioRepo.delete(usuario);
    }

    public List<Usuario> buscarPorNombre(String nombre) {
        return usuarioRepo.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Usuario> buscarPorRol(String rol) {

        List<Usuario> usuarios = usuarioRepo.findByRolContainingIgnoreCase(rol);

        if (usuarios.isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No existen usuarios con el rol: " + rol
            );
        }

        return usuarios;
    }
}