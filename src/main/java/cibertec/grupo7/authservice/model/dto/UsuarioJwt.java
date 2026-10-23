package cibertec.grupo7.authservice.model.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Builder
@Data
public class UsuarioJwt {
    private Integer idusuario;
    private String nomusuario;
    private String email;
    private List<String> roles;
    private String token;
}