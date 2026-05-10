package com.example.musicupc.services;

import com.example.musicupc.entities.Usuario;
import com.example.musicupc.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepo;
    public UsuarioService(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    public List<Usuario> listar() {
        return usuarioRepo.findAll();
    }

    public Usuario listarPorId(Long id) {
        return usuarioRepo.findById(id).
                orElseThrow(() -> new RuntimeException("El usuario con id: " + id + " no existe."));
    }

    public Usuario registrar(Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank())
        {
            throw new IllegalArgumentException("El nombre del usuario es obligatorio.");
        }
        if (usuario.getApellido() == null || usuario.getApellido().isBlank()){
            throw new IllegalArgumentException("El apellido del usuario es obligatorio.");
        }
        if (usuario.getEmail() == null || !usuario.getEmail().contains("@")){
            throw new IllegalArgumentException("El email no es válido.");
        }
        if (usuarioRepo.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (usuario.getTelefono() == null || usuario.getTelefono().length() != 9){
            throw new IllegalArgumentException("El telefono debe tener 9 dígitos.");
        }
        if (usuario.getDni() == null || usuario.getDni().length() != 8){
            throw new IllegalArgumentException("El dni debe tener 8 dígitos.");
        }
        if (usuarioRepo.existsByDni(usuario.getDni())) {
            throw new RuntimeException("El DNI ya está registrado");
        }
        if (usuario.getRol() == null || usuario.getRol().isBlank()){
            throw new IllegalArgumentException("El rol del usuario es obligatorio.");
        }
        if(usuario.getFechaNacimiento() == null){
            throw new IllegalArgumentException("La fecha de nacimiento del usuario es obligatoria.");
        }
        return usuarioRepo.save(usuario);
    }

    public Usuario actualizar(Long id, Usuario usuario) {
        Usuario existente = listarPorId(id);

        existente.setNombre(usuario.getNombre());
        existente.setApellido(usuario.getApellido());
        existente.setEmail(usuario.getEmail());
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
}