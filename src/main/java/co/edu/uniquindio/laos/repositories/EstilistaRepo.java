package co.edu.uniquindio.laos.repositories;

import co.edu.uniquindio.laos.model.Estilista;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstilistaRepo extends MongoRepository<Estilista, String> {

    @NotNull
    Optional<Estilista> findById(@NotNull String id);
}
