package co.edu.uniquindio.laos.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("citas")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cita {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String usuarioId;
    private String estilistaId;
    private String servicioId;
    private LocalDateTime fechaHora;
    private EstadoCita estado;
}
