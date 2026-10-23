package cibertec.grupo7.authservice.controller;

import cibertec.grupo7.authservice.model.dto.*;
import cibertec.grupo7.authservice.model.entity.Rol;
import cibertec.grupo7.authservice.model.entity.Usuario;
import cibertec.grupo7.authservice.security.JwtService;
import cibertec.grupo7.authservice.service.DetalleUsuarioService;
import cibertec.grupo7.authservice.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final DetalleUsuarioService detalleUsuarioService;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<GenericResponse<UsuarioJwt>> login(@RequestBody Login login) {
        GenericResponse<UsuarioJwt> response;
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login.getNomusuario(), login.getPassword())
            );
            Usuario usuario = usuarioService.getUsuarioByNomusuario(login.getNomusuario());
            String token = jwtService.generarToken(usuario, detalleUsuarioService.getAuthorities(usuario.getRoles()));

            response = GenericResponse.<UsuarioJwt>builder()
                    .response(UsuarioJwt.builder()
                            .idusuario(usuario.getIdusuario())
                            .nomusuario(usuario.getNomusuario())
                            .email(usuario.getEmail())
                            .roles(usuario.getRoles().stream().map(Rol::getRol).toList())
                            .token(token)
                            .build())
                    .build();
            return ResponseEntity.ok(response);

        } catch (AuthenticationException ex) {
            response = GenericResponse.<UsuarioJwt>builder()
                    .error(ErrorMessage.builder()
                            .message("Usuario y/o password incorrecto o cuenta inactiva")
                            .build())
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<GenericResponse<String>> registrar(@RequestBody UsuarioRegistroDto dto) {
        try {
            Usuario creado = usuarioService.registrarCliente(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(GenericResponse.<String>builder()
                            .response("Cliente " + creado.getEmail() + " registrado exitosamente.")
                            .build());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GenericResponse.<String>builder()
                            .error(ErrorMessage.builder().message(ex.getMessage()).build())
                            .build());
        }
    }
}