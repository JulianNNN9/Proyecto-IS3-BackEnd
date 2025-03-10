package co.edu.uniquindio.laos.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document("quejas")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Queja {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String clienteId;
    private String descripcion;
    private LocalDateTime fecha;
    private EstadoQueja estadoQueja;
    private String nombreServicio;
    private String nombreEstilista;
    private RespuestaQueja respuestaQueja;
}
