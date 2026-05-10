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

    public Usuario actualizar(Long id, Usuario usuario) {
        Usuario existente = listarPorId(id);

        existente.setNombre(usuario.getNombre());
        existente.setApellido(usuario.getApellido());
        existente.setEmail(usuario.getEmail());
        existente.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        existente.setTelefono(usuario.getTelefono());
        existente.setDni(usuario.getDni());
        existente.setRol(usuario.getRol());
        existente.setFechaNacimiento(usuario.getFechaNacimiento());

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