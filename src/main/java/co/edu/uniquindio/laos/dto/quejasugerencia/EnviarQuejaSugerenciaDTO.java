package co.edu.uniquindio.laos.dto.quejasugerencia;

import java.time.LocalDateTime;

public record EnviarQuejaSugerenciaDTO(
        String tipo,
        String cliente,
        String descripcion

){
}
