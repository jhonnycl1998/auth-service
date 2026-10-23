package cibertec.grupo7.authservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRegistroDto {
    private String nomusuario;
    private String email;
    private String password;
    private String nombres;
    private String apellidos;
    private List<Integer> rolesIds;
}
