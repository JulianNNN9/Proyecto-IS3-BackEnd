package co.edu.uniquindio.laos.controllers;

        import co.edu.uniquindio.laos.dto.MensajeDTO;
        import co.edu.uniquindio.laos.dto.cita.InformacionCitaDTO;
        import co.edu.uniquindio.laos.services.interfaces.CitasService;
        import io.swagger.v3.oas.annotations.security.SecurityRequirement;
        import lombok.RequiredArgsConstructor;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.*;

        import java.util.List;

        /**
         * Controlador REST para operaciones relacionadas con los estilistas.
         * Gestiona funcionalidades específicas para estilistas como:
         * - Consulta de citas asignadas
         * - Visualización de detalles de citas
         * - Filtrado de citas por estado
         *
         * Requiere autenticación con token JWT y rol de ESTILISTA.
         */
        @RestController
        @RequestMapping("/api/estilista")
        @SecurityRequirement(name = "bearerAuth")
        @RequiredArgsConstructor
        public class EstilistaControlador {

            /**
             * Servicio para la gestión de citas
             */
            private final CitasService citasService;

            /**
             * Obtiene todas las citas asignadas a un estilista específico
             * @param estilistaId Identificador único del estilista
             * @return Lista de citas asociadas al estilista
             */
            @GetMapping("/citas/mis-citas/{estilistaId}")
            public ResponseEntity<MensajeDTO<List<InformacionCitaDTO>>> obtenerMisCitas(@PathVariable String estilistaId) {
                List<InformacionCitaDTO> citas = citasService.obtenerCitasPorEstilistaId(estilistaId);
                return ResponseEntity.ok().body(new MensajeDTO<>(false, citas));
            }

            /**
             * Obtiene información detallada de una cita específica
             * @param citaId Identificador único de la cita
             * @return Datos completos de la cita solicitada
             * @throws Exception Si la cita no existe o no es accesible para el estilista
             */
            @GetMapping("/citas/{citaId}")
            public ResponseEntity<MensajeDTO<InformacionCitaDTO>> obtenerDetalleCita(@PathVariable String citaId) throws Exception {
                InformacionCitaDTO cita = citasService.obtenerCitaPorId(citaId);
                return ResponseEntity.ok().body(new MensajeDTO<>(false, cita));
            }

            /**
             * Filtra citas del estilista según su estado actual
             * @param estado Estado de la cita (PROGRAMADA, CANCELADA, COMPLETADA, etc.)
             * @return Lista de citas que coinciden con el estado especificado
             */
            @GetMapping("/citas/estado/{estado}")
            public ResponseEntity<MensajeDTO<List<InformacionCitaDTO>>> obtenerCitasPorEstado(@PathVariable String estado) {
                List<InformacionCitaDTO> citas = citasService.obtenerCitasPorEstado(estado);
                return ResponseEntity.ok().body(new MensajeDTO<>(false, citas));
            }
        }