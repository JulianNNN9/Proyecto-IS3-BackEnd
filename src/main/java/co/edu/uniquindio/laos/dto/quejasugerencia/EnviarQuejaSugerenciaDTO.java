package co.edu.uniquindio.laos.dto.quejasugerencia;

import jakarta.validation.constraints.NotBlank;

public record EnviarQuejaSugerenciaDTO(
        @NotBlank String tipo,
        String cliente,
        @NotBlank String descripcion

){
}
