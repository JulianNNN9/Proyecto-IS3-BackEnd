package co.edu.uniquindio.laos.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class CodigoRecuperacion {

    private String codigo;
    private LocalDateTime fechaCreacion;
}

