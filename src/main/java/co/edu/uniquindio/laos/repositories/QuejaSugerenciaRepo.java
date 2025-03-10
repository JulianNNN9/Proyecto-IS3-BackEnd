package co.edu.uniquindio.laos.repositories;

import co.edu.uniquindio.laos.dto.quejasugerencia.QuejasPorClienteDTO;
import co.edu.uniquindio.laos.dto.quejasugerencia.QuejasPorTipoDTO;
import co.edu.uniquindio.laos.model.QuejaSugerencia;
import co.edu.uniquindio.laos.model.TipoPQRS;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface QuejaSugerenciaRepo extends MongoRepository<QuejaSugerencia, String> {

    // Contar número de quejas por tipo
    @Aggregation(pipeline = {
            "{ $group: { _id: '$tipoPQRS', count: { $sum: 1 } } }"
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

    // Contar cantidad de quejas por usuario (considerando ANONIMO si no tiene usuario)
    @Aggregation(pipeline = {
            "{ $group: { _id: { $ifNull: ['$usuario._id', 'ANONIMO'] }, count: { $sum: 1 } } }"
    })
    List<Map<String, Object>> contarQuejasPorUsuarioRaw();

    default List<QuejasPorClienteDTO> contarQuejasPorUsuario() {
        return contarQuejasPorUsuarioRaw().stream()
                .map(entry -> new QuejasPorClienteDTO(
                        (String) entry.get("_id"),
                        ((Number) entry.get("count")).longValue()
                ))
                .collect(Collectors.toList());
    }

    // Contar número de quejas/sugerencias de un usuario por tipo
    @Aggregation(pipeline = {
            "{ $match: { 'usuario._id': ObjectId(?0) } }",
            "{ $group: { _id: '$tipoPQRS', count: { $sum: 1 } } }"
    })
    List<Map<String, Object>> contarQuejasPorUsuarioYTipoRaw(String usuarioId);

    default List<QuejasPorTipoDTO> contarQuejasPorUsuarioYTipo(String usuarioId) {
        return contarQuejasPorUsuarioYTipoRaw(usuarioId).stream()
                .map(entry -> new QuejasPorTipoDTO(
                        (String) entry.get("_id"),
                        ((Number) entry.get("count")).longValue()
                ))
                .collect(Collectors.toList());
    }
}