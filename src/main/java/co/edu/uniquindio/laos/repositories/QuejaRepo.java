package co.edu.uniquindio.laos.repositories;

import co.edu.uniquindio.laos.model.Queja;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuejaRepo extends MongoRepository<Queja, String> {


    List<Queja> findAll();

    @Query("{ 'servicioId' : ?0 }")
    List<Queja> findByServicioId(String servicioId);

    @Query("{ 'clienteId' : ?0 }")
    List<Queja> findByClienteId(String clienteId);

    @Query("{ 'estadoQueja' : ?0 }")
    List<Queja> findByEstadoQueja(String estadoQueja);

    @Query("{ 'fecha' : { $gte: ?0, $lte: ?1 } }")
    List<Queja> findByFechaBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'fecha' : ?0 }")
    List<Queja> findByFecha(LocalDateTime fecha);

}