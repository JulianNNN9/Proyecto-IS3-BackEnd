package co.edu.uniquindio.laos.services.implementation;

    import co.edu.uniquindio.laos.dto.cita.CrearCitaDTO;
    import co.edu.uniquindio.laos.dto.cita.InformacionCitaDTO;
    import co.edu.uniquindio.laos.dto.cita.ReprogramarCitaDTO;
    import co.edu.uniquindio.laos.dto.cita.CalendarioCitasDTO;
    import co.edu.uniquindio.laos.exceptions.HorarioYEstilistaOcupadoException;
    import co.edu.uniquindio.laos.exceptions.RecursoNoEncontradoException;
    import co.edu.uniquindio.laos.model.Cita;
    import co.edu.uniquindio.laos.model.EstadoCita;
    import co.edu.uniquindio.laos.model.Estilista;
    import co.edu.uniquindio.laos.model.Servicio;
    import co.edu.uniquindio.laos.repositories.CitaRepo;
    import co.edu.uniquindio.laos.repositories.ServiciosRepo;
    import co.edu.uniquindio.laos.repositories.EstilistaRepo;
    import co.edu.uniquindio.laos.services.interfaces.CitasService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.Arrays;
    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;

    /**
     * Implementación del servicio de citas que maneja la lógica de negocio
     * para la gestión de citas en el sistema.
     *
     * Esta clase se encarga de crear, reprogramar y cancelar citas,
     * así como de consultar la información de citas según diferentes criterios.
     */
    @Service
    @Transactional
    @RequiredArgsConstructor
    public class CitasServiceImple implements CitasService {

        /**
         * Repositorio para el acceso y persistencia de citas en la base de datos
         */
        @Autowired
        private CitaRepo citaRepo;
        @Autowired
        private EstilistaRepo estilistaRepository;
        @Autowired
        private ServiciosRepo servicioRepository;



        /**
         * Crea una nueva cita en el sistema verificando disponibilidad
         * @param crearCitaDTO Datos necesarios para crear la cita
         * @return Identificador único de la cita creada
         * @throws Exception Si el estilista ya tiene una cita programada en ese horario
         */
        @Override
        public String crearCita(CrearCitaDTO crearCitaDTO) throws Exception {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime newFechaHora = LocalDateTime.parse(crearCitaDTO.fechaHora(), formatter);

            // Verifica si el estilista ya tiene una cita en ese horario
            if(citaRepo.existsByEstilistaIdAndFechaHora(crearCitaDTO.idEstilista(), newFechaHora)) {
                throw new HorarioYEstilistaOcupadoException("El estilista ya tiene una cita programada en ese horario");
            }

            // Construye y guarda la nueva cita
            Cita cita = Cita.builder()
                    .usuarioId(crearCitaDTO.idCliente())
                    .estilistaId(crearCitaDTO.idEstilista())
                    .servicioId(crearCitaDTO.idServicio())
                    .fechaHora(newFechaHora)
                    .estado(EstadoCita.CONFIRMADA)
                    .build();

            citaRepo.save(cita);
            return cita.getId();
        }

        /**
         * Cancela una cita existente cambiando su estado
         * @param idCita Identificador único de la cita a cancelar
         * @return Identificador de la cita cancelada
         * @throws Exception Si la cita no existe
         */
        @Override
        public String cancelarCita(String idCita) throws Exception {
            Optional<Cita> optionalCita = citaRepo.findById(idCita);

            if (optionalCita.isEmpty()) {
                throw new RecursoNoEncontradoException("No existe una cita con el id: " + idCita);
            }

            Cita cita = optionalCita.get();
            cita.setEstado(EstadoCita.CANCELADA);
            citaRepo.save(cita);

            return idCita;
        }

        /**
         * Cambia la fecha y hora de una cita existente
         * @param reprogramarCitaDTO Datos para la reprogramación con el nuevo horario
         * @return Identificador de la cita reprogramada
         * @throws Exception Si la cita no existe o el horario no está disponible
         */
        @Override
        public String reprogramarCita(ReprogramarCitaDTO reprogramarCitaDTO) throws Exception {
            Optional<Cita> optionalCita = citaRepo.findById(reprogramarCitaDTO.citaId());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            if (optionalCita.isEmpty()) {
                throw new RecursoNoEncontradoException("No existe una cita con el id: " + reprogramarCitaDTO.citaId());
            }

            LocalDateTime newFechaHora = LocalDateTime.parse(reprogramarCitaDTO.nuevaFechaHora(), formatter);

            // Verifica disponibilidad del estilista en el nuevo horario
            if(citaRepo.existsByEstilistaIdAndFechaHora(optionalCita.get().getEstilistaId(), newFechaHora)) {
                throw new HorarioYEstilistaOcupadoException("El estilista ya tiene una cita programada en ese horario");
            }

            // Actualiza la cita con la nueva fecha y estado
            Cita cita = optionalCita.get();
            cita.setFechaHora(newFechaHora);
            cita.setEstado(EstadoCita.REPROGRAMADA);
            citaRepo.save(cita);

            return reprogramarCitaDTO.citaId();
        }

        /**
         * Recupera todas las citas asociadas a un cliente específico
         * @param clienteId Identificador único del cliente
         * @return Lista de citas del cliente
         */
        @Override
        public List<InformacionCitaDTO> obtenerCitasPorClienteId(String clienteId) {
            List<String> estadosPermitidos = Arrays.asList("CONFIRMADA", "REPROGRAMADA");
            List<Cita> citas = citaRepo.findByUsuarioIdAndEstadoIn(clienteId, estadosPermitidos);
            return citas.stream()
                    .map(this::convertirCitaADTO)
                    .collect(Collectors.toList());
        }

        /**
         * Recupera las citas canceladas y completadas de un cliente específico
         * @param clienteId Identificador único del cliente
         * @return Lista de citas canceladas o completadas del cliente
         */
        @Override
        public List<InformacionCitaDTO> obtenerCitasCanceladasYCompletadasPorClienteId(String clienteId) {
            List<String> estados = Arrays.asList("CANCELADA", "COMPLETADA");
            List<Cita> citas = citaRepo.findByUsuarioIdAndEstadoIn(clienteId, estados);
            return citas.stream()
                    .map(this::convertirCitaADTO)
                    .collect(Collectors.toList());
        }

        /**
         * Recupera todas las citas asignadas a un estilista específico
         * @param estilistaId Identificador único del estilista
         * @return Lista de citas del estilista
         */
        @Override
        public List<InformacionCitaDTO> obtenerCitasPorEstilistaId(String estilistaId) {
            List<Cita> citas = citaRepo.findByEstilistaId(estilistaId);
            return citas.stream()
                    .map(this::convertirCitaADTO)
                    .collect(Collectors.toList());
        }

        /**
         * Recupera la información de una cita específica por su identificador
         * @param citaId Identificador único de la cita
         * @return Información detallada de la cita
         * @throws Exception Si la cita no existe
         */
        @Override
        public InformacionCitaDTO obtenerCitaPorId(String citaId) throws Exception {
            Optional<Cita> optionalCita = citaRepo.findById(citaId);

            if (optionalCita.isEmpty()) {
                throw new RecursoNoEncontradoException("No existe una cita con el id: " + citaId);
            }

            return convertirCitaADTO(optionalCita.get());
        }

        /**
         * Recupera todas las citas que tienen un estado específico
         * @param estado Estado de las citas a buscar (CONFIRMADA, CANCELADA, REPROGRAMADA, etc.)
         * @return Lista de citas que coinciden con el estado indicado
         */
        @Override
        public List<InformacionCitaDTO> obtenerCitasPorEstado(String estado) {
            try {
                EstadoCita estadoCita = EstadoCita.valueOf(estado.toUpperCase());
                List<Cita> citas = citaRepo.findByEstado(estadoCita);
                return citas.stream()
                        .map(this::convertirCitaADTO)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                return List.of();
            }
        }

        /**
         * Convierte una entidad Cita en un objeto DTO para su transferencia
         * @param cita Entidad de cita a convertir
         * @return Objeto DTO con la información relevante de la cita
         */
        private InformacionCitaDTO convertirCitaADTO(Cita cita) {

            String nombreEstilista = "Vacio";

            // Obtener nombre del estilista
            Optional<Estilista> estilistaOptional = estilistaRepository.findById(cita.getEstilistaId());

            if (estilistaOptional.isPresent()){
                nombreEstilista = estilistaOptional.get().getNombre();
            }

            String nombreServicio = "Vacio";
            // Obtener nombre del servicio
            Optional<Servicio> servicioOptional = servicioRepository.findById(cita.getServicioId());

            if (servicioOptional.isPresent()){
                nombreServicio = servicioOptional.get().getNombre();
            }

            return new InformacionCitaDTO(
                    cita.getId(),
                    cita.getUsuarioId(),
                    cita.getEstilistaId(),
                    nombreEstilista,
                    cita.getServicioId(),
                    nombreServicio,
                    cita.getFechaHora(),
                    cita.getEstado()
            );
        }

        @Override
        public List<CalendarioCitasDTO> obtenerCitasConfirmadasYReprogramadas() {
            List<Cita> citas = citaRepo.findByEstadoIn(List.of("CONFIRMADA", "REPROGRAMADA"));

            return citas.stream()
                    .map(this::convertirACalendarioCitasDTO)
                    .toList();
        }

        public CalendarioCitasDTO convertirACalendarioCitasDTO(Cita cita) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            String fechaHoraInicio = cita.getFechaHora().format(formatter);
            String fechaHoraFin = cita.getFechaHora().plusHours(1).format(formatter); // Suma 1 hora

            return new CalendarioCitasDTO(fechaHoraInicio, fechaHoraFin);
        }

    }