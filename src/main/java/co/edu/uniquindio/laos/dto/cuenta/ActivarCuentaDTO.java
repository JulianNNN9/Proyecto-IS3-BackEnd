package co.edu.uniquindio.laos.dto.cuenta;

import jakarta.validation.constraints.NotBlank;

public record ActivarCuentaDTO(
        @NotBlank(message = "El correo no puede estar vacío")
        String email,
        @NotBlank(message = "El código de activación no puede estar vacío")
        String codigoActivacion
) {
}
