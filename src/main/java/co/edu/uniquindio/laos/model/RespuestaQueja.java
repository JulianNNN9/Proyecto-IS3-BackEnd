package co.edu.uniquindio.laos.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class RespuestaQueja {
    private String respuesta;
    private LocalDateTime fechaRespuesta;
}
