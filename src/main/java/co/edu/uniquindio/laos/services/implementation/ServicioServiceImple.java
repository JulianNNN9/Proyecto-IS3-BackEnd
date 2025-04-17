package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.model.Estilista;
import co.edu.uniquindio.laos.model.Servicio;
import co.edu.uniquindio.laos.repositories.ServiciosRepo;
import co.edu.uniquindio.laos.services.interfaces.ServicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de servicios que maneja la lógica de negocio
 * para la gestión de los servicios ofrecidos en el sistema.
 *
 * Esta clase se encarga de proporcionar operaciones relacionadas con los
 * servicios disponibles en el establecimiento, como la obtención de todos
 * los servicios para su visualización y selección por parte de los clientes.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ServicioServiceImple implements ServicioService {

    /**
     * Repositorio para el acceso y persistencia de servicios en la base de datos
     */
    private final ServiciosRepo serviciosRepo;

    /**
     * Recupera la lista completa de todos los servicios disponibles en el sistema
     *
     * Este método permite obtener información sobre todos los servicios
     * que se ofrecen a los clientes en el establecimiento.
     *
     * @return Lista de objetos Servicio con la información de todos los servicios disponibles
     */
    @Override
    public List<Servicio> obtenerTodosLosServicios() {
        return serviciosRepo.findAll();
    }
}