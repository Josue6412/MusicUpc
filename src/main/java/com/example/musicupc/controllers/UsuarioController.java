package com.example.musicupc.controllers;

import com.example.musicupc.dtos.UsuarioDTOInsert;
import com.example.musicupc.dtos.UsuarioDTOList;
import com.example.musicupc.entities.Usuario;
import com.example.musicupc.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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

    @PostMapping("/{id}/foto")
    public UsuarioDTOList subirFotoPerfil(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        Usuario usuario = usuarioService.listarPorId(id);
        String fotoAnterior = usuario.getFotoPerfil();

        String contentType = file.getContentType();

        if (contentType == null ||
                (!contentType.equals("image/png")
                        && !contentType.equals("image/jpeg")
                        && !contentType.equals("image/jpg")
                        && !contentType.equals("image/webp"))) {
            throw new RuntimeException("Solo se permiten imágenes PNG, JPG, JPEG o WEBP.");
        }

        String nombreOriginal = file.getOriginalFilename();
        String extension = "";

        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(nombreOriginal.lastIndexOf(".")).toLowerCase();
        }

        if (extension.isBlank()) {
            if (contentType.equals("image/png")) {
                extension = ".png";
            } else if (contentType.equals("image/jpeg") || contentType.equals("image/jpg")) {
                extension = ".jpg";
            } else if (contentType.equals("image/webp")) {
                extension = ".webp";
            }
        }

        String nombreArchivo = UUID.randomUUID() + extension;

        Path carpeta = Paths.get("uploads/perfiles");
        Files.createDirectories(carpeta);

        Path rutaArchivo = carpeta.resolve(nombreArchivo);
        Files.write(rutaArchivo, file.getBytes());

        String url = "https://musicupc.onrender.com/uploads/perfiles/" + nombreArchivo;

        usuario.setFotoPerfil(url);
        Usuario actualizado = usuarioService.actualizar(id, usuario, true);

        eliminarFotoAnterior(fotoAnterior);

        return convertToDTO(actualizado);
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

    private void eliminarFotoAnterior(String fotoAnterior) {
        if (fotoAnterior == null || fotoAnterior.isBlank()) {
            return;
        }

        if (!fotoAnterior.contains("/uploads/perfiles/")) {
            return;
        }

        try {
            String nombreArchivo = fotoAnterior.substring(fotoAnterior.lastIndexOf("/") + 1);

            Path rutaArchivo = Paths.get("uploads", "perfiles", nombreArchivo);

            Files.deleteIfExists(rutaArchivo);
        } catch (Exception e) {
            System.out.println("No se pudo eliminar la foto anterior: " + e.getMessage());
        }
    }
}
