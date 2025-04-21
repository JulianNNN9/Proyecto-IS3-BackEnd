package co.edu.uniquindio.laos.services.implementation;

    import co.edu.uniquindio.laos.dto.sugerencias.CrearSugerenciaDTO;
    import co.edu.uniquindio.laos.dto.sugerencias.SugerenciaDTO;
    import co.edu.uniquindio.laos.model.Sugerencia;
    import co.edu.uniquindio.laos.model.Usuario;
    import co.edu.uniquindio.laos.repositories.SugerenciaRepository;
    import co.edu.uniquindio.laos.repositories.UsuarioRepo;
    import co.edu.uniquindio.laos.services.interfaces.SugerenciaService;
    import co.edu.uniquindio.laos.services.interfaces.UsuarioService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import java.time.format.DateTimeFormatter;

    import java.time.LocalDate;
    import java.util.List;
    import java.util.stream.Collectors;

    /**
     * Implementación del servicio de sugerencias que maneja la lógica de negocio
     * para la gestión de comentarios y sugerencias de los usuarios en el sistema.
     *
     * Esta clase se encarga de crear, consultar y actualizar el estado de las sugerencias
     * enviadas por los clientes para mejorar los servicios ofrecidos.
     */
    @Service
    @Transactional
    @RequiredArgsConstructor
    public class SugerenciaServiceImple implements SugerenciaService {

        /**
         * Repositorio para el acceso y persistencia de sugerencias en la base de datos
         */
        private final SugerenciaRepository sugerenciaRepository;

        /**
         * Repositorio para el acceso a los datos de usuarios
         */
        private final UsuarioRepo usuarioRepo;

        /**
         * Formateador para manejar fechas en el formato adecuado
         */
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        /**
         * Crea una nueva sugerencia en el sistema con la información proporcionada
         *
         * Este método registra las sugerencias enviadas por los clientes incluyendo
         * sus datos de contacto y el mensaje con la sugerencia o comentario.
         *
         * @param dto Datos necesarios para crear la sugerencia
         * @return Identificador único de la sugerencia creada
         */
        @Override
        public String crearSugerencia(CrearSugerenciaDTO dto) {

            Sugerencia nuevaSugerencia = new Sugerencia();

            nuevaSugerencia.setNombre(dto.nombre());
            nuevaSugerencia.setEmail(dto.email());
            nuevaSugerencia.setMotivo(dto.motivo());
            nuevaSugerencia.setMensaje(dto.mensaje());
            nuevaSugerencia.setFecha(LocalDate.now().format(formatter)); // Fecha actual en String
            nuevaSugerencia.setRevisado(false);

            Sugerencia guardada = sugerenciaRepository.save(nuevaSugerencia);

            return guardada.getId();
        }

        /**
         * Recupera la lista completa de todas las sugerencias registradas en el sistema
         *
         * @return Lista de objetos SugerenciaDTO con la información de todas las sugerencias
         */
        @Override
        public List<SugerenciaDTO> obtenerSugerencias() {
            List<Sugerencia> sugerencias = sugerenciaRepository.findAll();

            return sugerencias.stream().map(sugerencia ->
                    new SugerenciaDTO(
                            sugerencia.getId(),
                            sugerencia.getNombre(),
                            sugerencia.getEmail(),
                            sugerencia.getMotivo(),
                            sugerencia.getMensaje(),
                            sugerencia.getFecha(),
                            sugerencia.isRevisado()
                    )
            ).collect(Collectors.toList());
        }

        /**
         * Actualiza el estado de una sugerencia a "revisada"
         *
         * Este método permite al personal administrativo marcar como revisada
         * una sugerencia después de haberla leído y analizado.
         *
         * @param id Identificador único de la sugerencia a actualizar
         */
        @Override
        public void marcarComoRevisado(String id) {
            sugerenciaRepository.findById(id).map(sugerencia -> {
                sugerencia.setRevisado(true);
                sugerenciaRepository.save(sugerencia);
                return true;
            });
        }
    }