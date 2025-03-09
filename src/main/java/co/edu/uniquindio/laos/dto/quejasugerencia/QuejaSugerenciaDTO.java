package co.edu.uniquindio.laos.dto.quejasugerencia;

import java.time.LocalDateTime;

public record QuejaSugerenciaDTO(
        String id,
        String tipo,
        String cliente,
        String descripcion,
        String estado,
        String respuesta,
        LocalDateTime fechaEnvio,
        LocalDateTime fechaRespuesta
){
}
