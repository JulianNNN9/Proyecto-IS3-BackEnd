package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.model.Estilista;
import co.edu.uniquindio.laos.repositories.EstilistaRepo;
import co.edu.uniquindio.laos.services.interfaces.EstilistaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de estilistas que maneja la lógica de negocio
 * para la gestión de estilistas en el sistema.
 *
 * Esta clase se encarga de proporcionar operaciones relacionadas con los
 * estilistas que trabajan en el establecimiento, incluyendo la recuperación
 * de información sobre todos los estilistas disponibles.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class EstilistaServiceImple implements EstilistaService {

    /**
     * Repositorio para el acceso y persistencia de estilistas en la base de datos
     */
    private final EstilistaRepo estilistaRepo;

    /**
     * Recupera la lista completa de todos los estilistas registrados en el sistema
     *
     * Este método permite obtener información sobre todos los profesionales
     * disponibles para realizar servicios en el establecimiento.
     *
     * @return Lista de objetos Estilista con la información de todos los estilistas
     */
    @Override
    public List<Estilista> obtenerTodosLosEstilistas() {
        return estilistaRepo.findAll();
    }
}