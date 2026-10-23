package cibertec.grupo7.authservice.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenericResponse<T> {
    private T response;
    private ErrorMessage error;
}
