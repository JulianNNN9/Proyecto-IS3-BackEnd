package co.edu.uniquindio.laos.repositories;

import co.edu.uniquindio.laos.dto.quejasugerencia.QuejasPorClienteDTO;
import co.edu.uniquindio.laos.dto.quejasugerencia.QuejasPorTipoDTO;
import co.edu.uniquindio.laos.model.QuejaSugerencia;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface QuejaSugerenciaRepo extends MongoRepository<QuejaSugerencia, String> {
    // Contar número de quejas por tipo
    @Aggregation(pipeline = {
            "{ $group: { _id: '$tipo', count: { $sum: 1 } } }"
    })
    List<Map<String, Object>> contarQuejasPorTipoRaw();

    default List<QuejasPorTipoDTO> contarQuejasPorTipo() {
        return contarQuejasPorTipoRaw().stream()
                .map(entry -> new QuejasPorTipoDTO(
                        (String) entry.get("_id"),
                        ((Number) entry.get("count")).longValue()
                ))
                .collect(Collectors.toList());
    }

    // Contar cantidad de quejas por cliente

    @Aggregation(pipeline = {
            "{ $group: { _id: '$cliente', count: { $sum: 1 } } }"
    })
    List<Map<String, Object>> contarQuejasPorClienteRaw();

        default List<QuejasPorClienteDTO> contarQuejasPorCliente() {
        return contarQuejasPorClienteRaw().stream()
                .map(entry -> new QuejasPorClienteDTO(
                        (String) entry.get("_id"),
                        ((Number) entry.get("count")).longValue()
                ))
                .collect(Collectors.toList());
    }

    // Contar número de quejas/sugerencias de un cliente por tipo
    @Aggregation(pipeline = {
            "{ $match: { cliente: ?0 } }",
            "{ $group: { _id: '$tipo', count: { $sum: 1 } } }"
    })
    List<Map<String, Object>> contarQuejasPorClienteYTipoRaw(String cliente);

    default List<QuejasPorTipoDTO> contarQuejasPorClienteYTipo(String cliente) {
        return contarQuejasPorClienteYTipoRaw(cliente).stream()
                .map(entry -> new QuejasPorTipoDTO(
                        (String) entry.get("_id"),
                        ((Number) entry.get("count")).longValue()
                ))
                .collect(Collectors.toList());
    }
}
