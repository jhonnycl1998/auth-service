package cibertec.grupo7.authservice.controller;

import cibertec.grupo7.authservice.model.dto.UsuarioRegistroDto;
import cibertec.grupo7.authservice.model.entity.Usuario;
import cibertec.grupo7.authservice.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody UsuarioRegistroDto dto) {
        try {
            usuarioService.crearUsuarioConRoles(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Usuario " + dto.getNomusuario() + " creado exitosamente.");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{idusuario}/rol")
    public ResponseEntity<Usuario> cambiarRol(
            @PathVariable Integer idusuario,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(usuarioService.cambiarRolUsuario(idusuario, body.get("rol")));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{idusuario}/activo")
    public ResponseEntity<Usuario> cambiarEstadoActivo(
            @PathVariable Integer idusuario,
            @RequestBody Map<String, Boolean> body) {
        return ResponseEntity.ok(usuarioService.cambiarEstadoActivo(idusuario, body.get("activo")));
    }
}