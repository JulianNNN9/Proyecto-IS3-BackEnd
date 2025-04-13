package co.edu.uniquindio.laos.services.implementation;

    import co.edu.uniquindio.laos.dto.cita.CrearCitaDTO;
    import co.edu.uniquindio.laos.dto.cita.InformacionCitaDTO;
    import co.edu.uniquindio.laos.dto.cita.ReprogramarCitaDTO;
    import co.edu.uniquindio.laos.exceptions.HorarioYEstilistaOcupadoException;
    import co.edu.uniquindio.laos.exceptions.RecursoNoEncontradoException;
    import co.edu.uniquindio.laos.model.Cita;
    import co.edu.uniquindio.laos.model.EstadoCita;
    import co.edu.uniquindio.laos.repositories.CitaRepo;
    import co.edu.uniquindio.laos.services.interfaces.CitasService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;

    @Service
    public class CitasServiceImple implements CitasService {

        @Autowired
        private CitaRepo citaRepo;

        @Override
        public String crearCita(CrearCitaDTO crearCitaDTO) throws Exception {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime newFechaHora = LocalDateTime.parse(crearCitaDTO.fechaHora(), formatter);

            // Aquí se verifica si la cita esta disponible en ese horario y con ese estilista
            if(citaRepo.existsByEstilistaIdAndFechaHora(crearCitaDTO.idEstilista(), newFechaHora)) {
                throw new HorarioYEstilistaOcupadoException("El estilista ya tiene una cita programada en ese horario");
            }

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

        @Override
        public String reprogramarCita(ReprogramarCitaDTO reprogramarCitaDTO) throws Exception {
            Optional<Cita> optionalCita = citaRepo.findById(reprogramarCitaDTO.citaId());

            if (optionalCita.isEmpty()) {
                throw new RecursoNoEncontradoException("No existe una cita con el id: " + reprogramarCitaDTO.citaId());
            }

            // Aquí se verifica si la cita esta disponible en ese horario y con ese estilista
            if(citaRepo.existsByEstilistaIdAndFechaHora(optionalCita.get().getEstilistaId(), LocalDateTime.parse(reprogramarCitaDTO.nuevaFechaHora()))) {
                throw new HorarioYEstilistaOcupadoException("El estilista ya tiene una cita programada en ese horario");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime newFechaHora = LocalDateTime.parse(reprogramarCitaDTO.nuevaFechaHora());

            Cita cita = optionalCita.get();
            cita.setFechaHora(newFechaHora);
            cita.setEstado(EstadoCita.REPROGRAMADA);
            citaRepo.save(cita);

            return reprogramarCitaDTO.citaId();
        }

        @Override
        public List<InformacionCitaDTO> obtenerCitasPorClienteId(String clienteId) {
            List<Cita> citas = citaRepo.findByUsuarioId(clienteId);
            return citas.stream()
                    .map(this::convertirCitaADTO)
                    .collect(Collectors.toList());
        }

        @Override
        public List<InformacionCitaDTO> obtenerCitasPorEstilistaId(String estilistaId) {
            List<Cita> citas = citaRepo.findByEstilistaId(estilistaId);
            return citas.stream()
                    .map(this::convertirCitaADTO)
                    .collect(Collectors.toList());
        }

        @Override
        public InformacionCitaDTO obtenerCitaPorId(String citaId) throws Exception {
            Optional<Cita> optionalCita = citaRepo.findById(citaId);

            if (optionalCita.isEmpty()) {
                throw new RecursoNoEncontradoException("No existe una cita con el id: " + citaId);
            }

            return convertirCitaADTO(optionalCita.get());
        }

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

        private InformacionCitaDTO convertirCitaADTO(Cita cita) {
            return new InformacionCitaDTO(
                    cita.getUsuarioId(),
                    cita.getEstilistaId(),
                    cita.getServicioId(),
                    cita.getFechaHora(),
                    cita.getEstado()
            );
        }
    }