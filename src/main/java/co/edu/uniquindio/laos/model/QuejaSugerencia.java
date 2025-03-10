package co.edu.uniquindio.laos.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("quejas_sugerencias")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class QuejaSugerencia {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    private TipoPQRS tipoPQRS;
    @DBRef
    private Usuario usuario;
    private String descripcion;
    private EstadoPQRS estadoPQRS;
    private String respuesta;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaRespuesta;
}
