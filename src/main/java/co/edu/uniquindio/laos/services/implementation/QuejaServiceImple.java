package co.edu.uniquindio.laos.services.implementation;

            import co.edu.uniquindio.laos.dto.queja.CrearQuejaDTO;
            import co.edu.uniquindio.laos.dto.queja.QuejaDTO;
            import co.edu.uniquindio.laos.model.*;
            import co.edu.uniquindio.laos.repositories.QuejaRepo;
            import co.edu.uniquindio.laos.services.interfaces.QuejaService;
            import co.edu.uniquindio.laos.exceptions.RecursoNoEncontradoException;
            import org.springframework.beans.factory.annotation.Autowired;
            import org.springframework.stereotype.Service;

            import java.time.LocalDateTime;
            import java.util.List;
            import java.util.Optional;
            import java.util.stream.Collectors;

            /**
             * Implementación del servicio de quejas que maneja la lógica de negocio
             * para la gestión de reclamos y quejas de los clientes en el sistema.
             *
             * Esta clase se encarga de crear, eliminar, consultar y responder a las quejas
             * presentadas por los clientes sobre los servicios recibidos.
             */
            @Service
            public class QuejaServiceImple implements QuejaService {

                /**
                 * Repositorio para el acceso y persistencia de quejas en la base de datos
                 */
                @Autowired
                private QuejaRepo quejaRepo;

                /**
                 * Crea una nueva queja en el sistema con la información proporcionada por el cliente
                 * @param crearQuejaDTO Datos necesarios para crear la queja
                 * @return Identificador único de la queja creada
                 * @throws Exception Si ocurre algún error durante el proceso de creación
                 */
                @Override
                public String crearQueja(CrearQuejaDTO crearQuejaDTO) throws Exception {
                    Queja queja = Queja.builder()
                            .clienteId(crearQuejaDTO.clienteId())
                            .nombreCliente(crearQuejaDTO.nombreCliente())
                            .descripcion(crearQuejaDTO.descripcion())
                            .fecha(crearQuejaDTO.fecha())
                            .estadoQueja(EstadoQueja.SIN_RESPONDER)
                            .nombreServicio(crearQuejaDTO.nombreServicio())
                            .nombreEstilista(crearQuejaDTO.nombreEstilista())
                            .respuestaQueja(null)
                            .build();

                    // Fix: Capture the returned entity after save
                    queja = quejaRepo.save(queja);
                    return queja.getId();
                }

                /**
                 * Elimina lógicamente una queja cambiando su estado a ELIMINADA
                 * @param idQueja Identificador único de la queja a eliminar
                 * @return Identificador de la queja eliminada
                 * @throws RecursoNoEncontradoException Si la queja no existe o ya ha sido respondida o eliminada
                 */
                @Override
                public String eliminarQueja(String idQueja) throws RecursoNoEncontradoException {
                    Queja queja = quejaRepo.findById(idQueja)
                            .orElseThrow(() -> new RecursoNoEncontradoException("Queja no encontrada"));
                    if(queja.getEstadoQueja().equals(EstadoQueja.RESPONDIDA)) {
                        throw new RecursoNoEncontradoException("No se puede eliminar una queja respondida");
                    }
                    if(queja.getEstadoQueja().equals(EstadoQueja.ELIMINADA)) {
                        throw new RecursoNoEncontradoException("La queja ya ha sido eliminada anteriormente");
                    }
                    queja.setEstadoQueja(EstadoQueja.ELIMINADA);
                    return idQueja;
                }

                /**
                 * Busca una queja específica por su identificador
                 * @param id Identificador único de la queja
                 * @return Objeto Queja encontrado
                 * @throws RecursoNoEncontradoException Si no existe una queja con ese ID o está eliminada
                 */
                @Override
                public Queja obtenerQuejaPorId(String id) throws RecursoNoEncontradoException {
                    Optional<Queja> QuejaExistente = quejaRepo.findById(id);

                    if (QuejaExistente.isEmpty()) {
                        throw new RecursoNoEncontradoException("Cupón no encontrado");
                    }

                    if (QuejaExistente.get().getEstadoQueja().equals(EstadoQueja.ELIMINADA)){
                        throw new RecursoNoEncontradoException("Cupón no encontrado");
                    }

                    Queja queja = QuejaExistente.get();
                    return queja;
                }

                /**
                 * Recupera todas las quejas asociadas a un servicio específico
                 * @param servicioId Identificador único del servicio
                 * @return Lista de quejas relacionadas con ese servicio
                 */
                @Override
                public List<Queja> obtenerListaQuejasPorServicioId(String servicioId) {
                    return quejaRepo.findByServicioId(servicioId);
                }

                /**
                 * Recupera todas las quejas presentadas por un cliente específico
                 * @param clienteId Identificador único del cliente
                 * @return Lista de quejas del cliente
                 */
                @Override
                public List<Queja> obtenerListaQuejasPorClienteId(String clienteId) {
                    return quejaRepo.findByClienteId(clienteId);
                }

                /**
                 * Recupera todas las quejas que tienen un estado específico
                 * @param estadoQueja Estado de las quejas a buscar (SIN_RESPONDER, RESPONDIDA, ELIMINADA)
                 * @return Lista de quejas que coinciden con el estado indicado
                 */
                @Override
                public List<Queja> obtenerListaQuejasPorEstado(EstadoQueja estadoQueja) {
                    return quejaRepo.findByEstadoQueja(String.valueOf(estadoQueja));
                }

                /**
                 * Recupera las quejas registradas dentro de un rango de fechas
                 * @param startDate Fecha de inicio del rango
                 * @param endDate Fecha de fin del rango
                 * @return Lista de quejas registradas en ese intervalo de tiempo
                 */
                @Override
                public List<Queja> obtenerListaQuejasPorFecha(LocalDateTime startDate, LocalDateTime endDate) {
                    return quejaRepo.findByFechaBetween(startDate, endDate);
                }

                /**
                 * Recupera las quejas registradas en una fecha específica
                 * @param fecha Fecha exacta de las quejas a buscar
                 * @return Lista de quejas registradas en esa fecha
                 */
                @Override
                public List<Queja> obtenerListaQuejasPorFechaUnica(LocalDateTime fecha) {
                    return quejaRepo.findByFecha(fecha);
                }

                /**
                 * Recupera todas las quejas que no han sido respondidas aún
                 * @return Lista de quejas pendientes de respuesta
                 */
                @Override
                public List<Queja> listarQuejas() {
                    return quejaRepo.findAll();
                }

                /**
                 * Registra una respuesta a una queja y cambia su estado a RESPONDIDA
                 *
                 * Este método permite al personal administrativo proporcionar una
                 * respuesta oficial a una queja presentada por un cliente.
                 *
                 * @param idQueja Identificador único de la queja a responder
                 * @param respuesta Texto de la respuesta a la queja
                 * @throws RecursoNoEncontradoException Si la queja no existe
                 */
                @Override
                public void responderQueja(String idQueja, String respuesta) throws RecursoNoEncontradoException {
                    Queja queja = quejaRepo.findById(idQueja)
                            .orElseThrow(() -> new RecursoNoEncontradoException("Queja no encontrada"));
                    queja.setRespuestaQueja(new RespuestaQueja(respuesta, LocalDateTime.now()));
                    queja.setEstadoQueja(EstadoQueja.RESPONDIDA);
                    quejaRepo.save(queja);
                }
            }