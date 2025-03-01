package co.edu.uniquindio.laos.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("servicios")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Servicio {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String nombre;
    private double precio;
    private int duracionMinutos;
}
