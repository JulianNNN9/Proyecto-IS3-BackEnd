package co.edu.uniquindio.laos.repositories;

import co.edu.uniquindio.laos.model.Servicio;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiciosRepo extends MongoRepository<Servicio, String> {

    @NotNull
    Optional<Servicio> findById(@NotNull String id);
}
