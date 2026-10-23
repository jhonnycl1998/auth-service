package cibertec.grupo7.authservice.config;

import cibertec.grupo7.authservice.model.entity.Rol;
import cibertec.grupo7.authservice.model.entity.Usuario;
import cibertec.grupo7.authservice.repository.RolRepository;
import cibertec.grupo7.authservice.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Rol rolAdmin = rolRepository.findByRol("ADMIN");
        if (rolAdmin == null) {
            rolAdmin = rolRepository.save(Rol.builder().rol("ADMIN").build());
        }

        Rol rolCliente = rolRepository.findByRol("CLIENTE");
        if (rolCliente == null) {
            rolCliente = rolRepository.save(Rol.builder().rol("CLIENTE").build());
        }

        // Valida por nomusuario
        if (!usuarioRepository.existsByNomusuario("admin")) {
            Usuario admin = Usuario.builder()
                    .nomusuario("admin")
                    .email("admin@cibertec.edu.pe")
                    .password(passwordEncoder.encode("123456"))
                    .nombres("Administrador")
                    .apellidos("General")
                    .activo(true)
                    .roles(Set.of(rolAdmin))
                    .build();

            usuarioRepository.save(admin);
            System.out.println(">>> Usuario ADMIN inicializado con éxito (username: admin / password: 123456) <<<");
        }
    }
}