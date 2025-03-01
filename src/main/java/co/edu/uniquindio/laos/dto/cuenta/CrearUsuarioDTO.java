package co.edu.uniquindio.laos.dto.cuenta;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record CrearUsuarioDTO(

    @NotBlank @Length(max = 10) String cedula,
    @NotBlank @Length(max = 30) String nombreCompleto,
    @Length(max = 50) String direccion,
    @Length(max = 10) String telefono,
    @NotBlank @Length(max = 100) @Email String email,
    @Pattern(regexp = "^[A-Z](.*[!@#$%^&*])$", message = "La contraseña debe comenzar con una letra mayúscula y terminar con un carácter especial.") @NotBlank @Length(min = 8 ) @Length(max = 24) String contrasenia

){
}
