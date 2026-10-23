package cibertec.grupo7.authservice.repository;

import cibertec.grupo7.authservice.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByNomusuario(String nomusuario);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNomusuarioOrEmail(String nomusuario, String email);

    boolean existsByNomusuario(String nomusuario);
    boolean existsByEmail(String email);
}