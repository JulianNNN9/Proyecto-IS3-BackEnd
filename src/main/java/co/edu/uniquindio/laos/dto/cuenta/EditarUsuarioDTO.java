package co.edu.uniquindio.laos.dto.cuenta;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record EditarUsuarioDTO(

        String idUsuario,
        @NotBlank @Length(max = 30) String nombreCompleto,
        @NotBlank @Length(max = 50) String direccion,
        @NotBlank @Length(max = 10) String telefono

) {
}
