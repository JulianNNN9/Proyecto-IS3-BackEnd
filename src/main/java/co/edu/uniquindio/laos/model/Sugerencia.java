package co.edu.uniquindio.laos.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("sugerencias")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Sugerencia {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String nombre;
    private String email;
    private String motivo;
    private String mensaje;
    private String fecha;
    private boolean revisado;
}
