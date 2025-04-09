package co.edu.uniquindio.laos.repositories;

import co.edu.uniquindio.laos.model.Cita;
import co.edu.uniquindio.laos.model.EstadoCita;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepo extends MongoRepository<Cita, String> {

    // Find all appointments by client id
    List<Cita> findByUsuarioId(String clienteId);

    // Find all appointments by stylist id
    List<Cita> findByEstilistaId(String estilistaId);

    // Find appointment by id (this is already provided by MongoRepository's findById)
    Optional<Cita> findById(String citaId);

    // Find all appointments by status
    List<Cita> findByEstado(EstadoCita estado);

    // Find all appointments by client id and status
    boolean existsByEstilistaIdAndFechaHora(String idEstilista, LocalDateTime fechaHora);
}