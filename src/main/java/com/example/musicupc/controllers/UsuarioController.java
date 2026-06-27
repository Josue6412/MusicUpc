package com.example.musicupc.controllers;

import com.example.musicupc.dtos.UsuarioDTOInsert;
import com.example.musicupc.dtos.UsuarioDTOList;
import com.example.musicupc.entities.Usuario;
import com.example.musicupc.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    public List<UsuarioDTOList> listar() {
        return usuarioService.listar().stream().map(this::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','USUARIO')")
    public UsuarioDTOList listarPorId(@PathVariable Long id) {
        return convertToDTO(usuarioService.listarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTOList registrar(@RequestBody UsuarioDTOInsert dto) {
        Usuario usuario = convertToEntity(dto);
        return convertToDTO(usuarioService.registrar(usuario));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @accountSecurity.isSelf(#id, authentication)")
    public UsuarioDTOList actualizar(@PathVariable Long id,
                                     @RequestBody UsuarioDTOInsert dto,
                                     Authentication authentication) {
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
        Usuario usuario = convertToEntity(dto);
        return convertToDTO(usuarioService.actualizar(id, usuario, esAdmin));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<UsuarioDTOList> buscarPorNombre(@RequestParam String nombre) {
        return usuarioService.buscarPorNombre(nombre)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/rol")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<UsuarioDTOList> buscarPorRol(@RequestParam String rol) {
        return usuarioService.buscarPorRol(rol).stream().map(this::convertToDTO).toList();
    }

    private UsuarioDTOList convertToDTO(Usuario usuario) {
        return new UsuarioDTOList(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getDni(),
                usuario.getRol(),
                usuario.getFechaNacimiento(),
                usuario.getFechaRegistro(),
                usuario.getFotoPerfil()
        );
    }

    private Usuario convertToEntity(UsuarioDTOInsert dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setContrasena(dto.getContrasena());
        usuario.setTelefono(dto.getTelefono());
        usuario.setDni(dto.getDni());
        usuario.setRol(dto.getRol());
        usuario.setFechaNacimiento(dto.getFechaNacimiento());
        usuario.setFotoPerfil(dto.getFotoPerfil());
        return usuario;
    }
}
