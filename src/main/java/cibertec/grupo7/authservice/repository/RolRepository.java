package cibertec.grupo7.authservice.repository;

import cibertec.grupo7.authservice.model.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    Rol findByRol(String nomrol);
}
