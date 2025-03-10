package co.edu.uniquindio.laos.repositories;

import co.edu.uniquindio.laos.model.Servicio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiciosRepo extends MongoRepository<Servicio, String> {
}
