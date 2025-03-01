package co.edu.uniquindio.laos.dto.cuenta;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record IniciarSesionDTO(

        @NotBlank(message = "El correo no puede estar vacío")
        @Email(message = "Debe proporcionar un correo electrónico válido")
        String email,

        @NotBlank(message = "La contraseña no puede estar vacía")
        @Length(min = 8 ) @Length(max = 24)
        String contrasenia

) {
}
