package co.edu.uniquindio.laos.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("faqs")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Faq {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String pregunta;
    private String respuesta;
}
