package co.edu.uniquindio.laos.repositories;

import co.edu.uniquindio.laos.model.Estilista;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstilistaRepo extends MongoRepository<Estilista, String> {
}
