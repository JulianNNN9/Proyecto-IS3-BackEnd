package co.edu.uniquindio.laos.dto.cuenta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record CambiarContraseniaDTO(

        String idUsuario,
        @NotBlank @Length(min = 8, max = 24) String contraseniaAntigua,
        @Pattern(regexp = "^[A-Z](.*[!@#$%^&*])$", message = "La contraseña debe comenzar con una letra mayúscula y terminar con un carácter especial.") @NotBlank @Length(min = 8, max = 24) String contraseniaNueva,
        @NotBlank @Length(min = 8, max = 24) String confirmarContraseniaNueva
) {
}
