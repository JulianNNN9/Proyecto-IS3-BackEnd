package co.edu.uniquindio.laos.repositories;

import co.edu.uniquindio.laos.model.Sugerencia;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SugerenciaRepository extends MongoRepository<Sugerencia, String> {

    // 🔹 Buscar sugerencias por fecha
    List<Sugerencia> findByFecha(String fecha);
}
