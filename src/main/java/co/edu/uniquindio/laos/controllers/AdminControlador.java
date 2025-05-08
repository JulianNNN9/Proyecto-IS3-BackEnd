package co.edu.uniquindio.laos.controllers;

    import co.edu.uniquindio.laos.dto.MensajeDTO;
    import co.edu.uniquindio.laos.dto.cita.InformacionCitaDTO;
    import co.edu.uniquindio.laos.dto.cuenta.EditarUsuarioDTO;
    import co.edu.uniquindio.laos.dto.cuenta.InformacionUsuarioDTO;
    import co.edu.uniquindio.laos.dto.cupon.CrearCuponDTO;
    import co.edu.uniquindio.laos.dto.cupon.CuponDTO;
    import co.edu.uniquindio.laos.dto.cupon.EditarCuponDTO;
    import co.edu.uniquindio.laos.dto.sugerencias.SugerenciaDTO;
    import co.edu.uniquindio.laos.exceptions.RecursoNoEncontradoException;
    import co.edu.uniquindio.laos.services.interfaces.*;
    import co.edu.uniquindio.laos.dto.queja.QuejaDTO;
    import co.edu.uniquindio.laos.model.EstadoQueja;
    import co.edu.uniquindio.laos.model.Queja;
    import io.swagger.v3.oas.annotations.security.SecurityRequirement;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.time.LocalDateTime;
    import java.util.List;

    /**
     * Controlador REST para las operaciones administrativas del sistema.
     * Gestiona todas las funcionalidades disponibles para los administradores:
     * - Administración de sugerencias
     * - Gestión de quejas y reclamos
     * - Supervisión y control de citas
     *
     * Requiere autenticación con token JWT y rol de ADMIN.
     */
    @RestController
    @RequestMapping("/api/admin")
    @SecurityRequirement(name = "bearerAuth")
    @RequiredArgsConstructor
    public class AdminControlador {

        /**
         * Servicio para la gestión de sugerencias
         */
        private final SugerenciaService sugerenciaService;

        /**
         * Servicio para la gestión de quejas y reclamos
         */
        private final QuejaService quejaService;

        /**
         * Servicio para la gestión de citas
         */
        private final CitasService citasService;

        /**
         * Servicio para la gestion de usuarios
         */
        private final UsuarioService usuarioService;

        private final CuponService cuponService;

        /**
         * Obtiene todas las sugerencias registradas en el sistema
         * @return Lista de todas las sugerencias
         */
        @GetMapping("/obtener-sugerencias")
        public ResponseEntity<MensajeDTO<List<SugerenciaDTO>>> obtenerSugerencias() {
            return ResponseEntity.ok().body(new MensajeDTO<>(false, sugerenciaService.obtenerSugerencias()));
        }

        /**
         * Marca una sugerencia como revisada por el administrador
         * @param id Identificador único de la sugerencia
         * @return Mensaje de confirmación
         */
        @PutMapping("/sugerencias/marcar-revisado")
        public ResponseEntity<MensajeDTO<String>> marcarComoRevisado(@RequestBody String id) {
            sugerenciaService.marcarComoRevisado(id);
            return ResponseEntity.ok().body(new MensajeDTO<>(false, "Sugerencia marcada como revisada correctamente"));
        }

        /**
         * Elimina una queja del sistema por su identificador
         * @param id Identificador único de la queja a eliminar
         * @return Mensaje de confirmación
         * @throws Exception Si la queja no existe o no puede ser eliminada
         */
        @DeleteMapping("/eliminar-queja/{id}")
        public ResponseEntity<MensajeDTO<String>> eliminarQueja(@PathVariable String id) throws Exception {
            quejaService.eliminarQueja(id);
            return ResponseEntity.ok().body(new MensajeDTO<>(false, "Queja eliminada correctamente"));
        }

        /**
         * Obtiene una queja específica por su identificador
         * @param id Identificador único de la queja
         * @return Datos completos de la queja solicitada
         * @throws Exception Si la queja no existe
         */
        @GetMapping("/obtener-queja/{id}")
        public ResponseEntity<MensajeDTO<Queja>> obtenerQuejaPorId(@PathVariable String id) throws Exception {
            Queja queja = quejaService.obtenerQuejaPorId(id);
            return ResponseEntity.ok().body(new MensajeDTO<>(false, queja));
        }

        /**
         * Obtiene quejas relacionadas con un servicio específico
         * @param servicioId Identificador del servicio
         * @return Lista de quejas asociadas al servicio
         */
        @GetMapping("/obtener-quejas-por/servicio")
        public ResponseEntity<MensajeDTO<List<Queja>>> obtenerQuejasPorServicioId(@RequestParam String servicioId) {
            List<Queja> quejas = quejaService.obtenerListaQuejasPorServicioId(servicioId);
            return ResponseEntity.ok().body(new MensajeDTO<>(false, quejas));
        }

        /**
         * Obtiene todas las quejas realizadas por un cliente específico
         * @param clienteId Identificador del cliente
         * @return Lista de quejas del cliente
         */
        @GetMapping("/obtener-quejas-por/cliente")
        public ResponseEntity<MensajeDTO<List<Queja>>> obtenerQuejasPorClienteId(@RequestParam String clienteId) {
            List<Queja> quejas = quejaService.obtenerListaQuejasPorClienteId(clienteId);
            return ResponseEntity.ok().body(new MensajeDTO<>(false, quejas));
        }

        /**
         * Filtra quejas según su estado actual
         * @param estadoQueja Estado de la queja (PENDIENTE, EN_PROCESO, RESUELTA, etc.)
         * @return Lista de quejas que coinciden con el estado especificado
         */
        @GetMapping("/obtener-quejas-por/estado")
        public ResponseEntity<MensajeDTO<List<Queja>>> obtenerQuejasPorEstado(@RequestParam String estadoQueja) {
            List<Queja> quejas = quejaService.obtenerListaQuejasPorEstado(EstadoQueja.valueOf(estadoQueja));
            return ResponseEntity.ok().body(new MensajeDTO<>(false, quejas));
        }

        /**
         * Obtiene quejas registradas dentro de un rango de fechas
         * @param startDate Fecha inicial del rango
         * @param endDate Fecha final del rango
         * @return Lista de quejas registradas en el periodo especificado
         */
        @GetMapping("/obtener-quejas-por/fecha")
        public ResponseEntity<MensajeDTO<List<Queja>>> obtenerQuejasPorFecha(@RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
            List<Queja> quejas = quejaService.obtenerListaQuejasPorFecha(startDate, endDate);
            return ResponseEntity.ok().body(new MensajeDTO<>(false, quejas));
        }

        /**
         * Obtiene quejas registradas en una fecha específica
         * @param fecha Fecha exacta para filtrar
         * @return Lista de quejas de la fecha indicada
         */
        @GetMapping("/obtener-quejas-por/fecha-unica")
        public ResponseEntity<MensajeDTO<List<Queja>>> obtenerQuejasPorFechaUnica(@RequestParam LocalDateTime fecha) {
            List<Queja> quejas = quejaService.obtenerListaQuejasPorFechaUnica(fecha);
            return ResponseEntity.ok().body(new MensajeDTO<>(false, quejas));
        }

        /**
         * Obtiene todas las quejas registradas en el sistema
         * @return Lista completa de quejas
         */
        @GetMapping("/obtener-quejas")
        public ResponseEntity<MensajeDTO<List<Queja>>> listarQuejas() {
            List<Queja> quejas = quejaService.listarQuejas();
            return ResponseEntity.ok().body(new MensajeDTO<>(false, quejas));
        }

        /**
         * Registra una respuesta a una queja y la marca como resuelta
         * @param idQueja Identificador de la queja a responder
         * @param respuesta Texto de respuesta para el cliente
         * @return Mensaje de confirmación
         * @throws RecursoNoEncontradoException Si la queja no existe
         */
        @PutMapping("/responder-queja/{idQueja}")
        public ResponseEntity<MensajeDTO<String>> responderQueja(@PathVariable String idQueja, @RequestBody String respuesta) throws RecursoNoEncontradoException {
            quejaService.responderQueja(idQueja, respuesta);
            return ResponseEntity.ok().body(new MensajeDTO<>(false, "Queja resuelta correctamente"));
        }

        /**
         * Obtiene todas las citas registradas en el sistema
         * @return Lista completa de citas
         */
        @GetMapping("/citas/todas")
        public ResponseEntity<MensajeDTO<List<InformacionCitaDTO>>> listarTodasCitas() {
            List<InformacionCitaDTO> citas = citasService.obtenerCitasPorEstado("TODAS");
            return ResponseEntity.ok().body(new MensajeDTO<>(false, citas));
        }

        /**
         * Filtra citas según su estado actual
         * @param estado Estado de la cita (PROGRAMADA, CANCELADA, COMPLETADA, etc.)
         * @return Lista de citas con el estado especificado
         */
        @GetMapping("/citas/por-estado")
        public ResponseEntity<MensajeDTO<List<InformacionCitaDTO>>> listarCitasPorEstado(@RequestParam String estado) {
            List<InformacionCitaDTO> citas = citasService.obtenerCitasPorEstado(estado);
            return ResponseEntity.ok().body(new MensajeDTO<>(false, citas));
        }

        /**
         * Obtiene todas las citas asignadas a un estilista específico
         * @param estilistaId Identificador del estilista
         * @return Lista de citas del estilista
         */
        @GetMapping("/citas/por-estilista/{estilistaId}")
        public ResponseEntity<MensajeDTO<List<InformacionCitaDTO>>> listarCitasPorEstilista(@PathVariable String estilistaId) {
            List<InformacionCitaDTO> citas = citasService.obtenerCitasPorEstilistaId(estilistaId);
            return ResponseEntity.ok().body(new MensajeDTO<>(false, citas));
        }

        /**
         * Obtiene todas las citas de un cliente específico
         * @param clienteId Identificador del cliente
         * @return Lista de citas del cliente
         */
        @GetMapping("/citas/por-cliente/{clienteId}")
        public ResponseEntity<MensajeDTO<List<InformacionCitaDTO>>> listarCitasPorCliente(@PathVariable String clienteId) {
            List<InformacionCitaDTO> citas = citasService.obtenerCitasPorClienteId(clienteId);
            return ResponseEntity.ok().body(new MensajeDTO<>(false, citas));
        }

        /**
         * Obtiene información detallada de una cita específica
         * @param citaId Identificador de la cita
         * @return Datos completos de la cita solicitada
         * @throws Exception Si la cita no existe
         */
        @GetMapping("/citas/{citaId}")
        public ResponseEntity<MensajeDTO<InformacionCitaDTO>> obtenerCitaPorId(@PathVariable String citaId) throws Exception {
            InformacionCitaDTO cita = citasService.obtenerCitaPorId(citaId);
            return ResponseEntity.ok().body(new MensajeDTO<>(false, cita));
        }

        /**
         * Cancela una cita programada
         * @param citaId Identificador de la cita a cancelar
         * @return Mensaje de confirmación con el ID de la cita cancelada
         * @throws Exception Si la cita no existe o no puede ser cancelada
         */
        @PutMapping("/citas/cancelar/{citaId}")
        public ResponseEntity<MensajeDTO<String>> cancelarCita(@PathVariable String citaId) throws Exception {
            String id = citasService.cancelarCita(citaId);
            return ResponseEntity.ok().body(new MensajeDTO<>(false, "Cita con id " + id + " cancelada correctamente"));
        }
        @GetMapping("/obtener-usuario/{codigo}")
        public ResponseEntity<MensajeDTO<InformacionUsuarioDTO>> obtenerInformacionUsuarioAdmin(@PathVariable String codigo) throws Exception{
            return ResponseEntity.ok().body( new MensajeDTO<>(false,
                    usuarioService.obtenerInformacionUsuario(codigo) ) );
        }
        @PutMapping("/editar-perfil")
        public ResponseEntity<MensajeDTO<String>> editarUsuarioAdmin(@Valid @RequestBody EditarUsuarioDTO editarUsuarioDTO)throws Exception{
            usuarioService.editarUsuario(editarUsuarioDTO);
            return ResponseEntity.ok().body( new MensajeDTO<>(false, "Cliente actualizado correctamente") );
        }

        @PostMapping ("/cupon/crear-cupon")
        public ResponseEntity<MensajeDTO<String>> crearCupon(@Valid @RequestBody CrearCuponDTO crearCuponDTO) throws Exception {
            return ResponseEntity.ok().body(new MensajeDTO<>(false, cuponService.crearCupon(crearCuponDTO)));
        }

        @PutMapping ("/cupon/editar-cupon")
        public ResponseEntity<MensajeDTO<String>> editarCupon(@Valid @RequestBody EditarCuponDTO editarCuponDTO) throws Exception {
            return ResponseEntity.ok().body(new MensajeDTO<>(false, cuponService.editarCupon(editarCuponDTO)));
        }

        @GetMapping ("/cupon/eliminar-cupon/{idCupon}")
        public ResponseEntity<MensajeDTO<String>> eliminarCupon(@PathVariable String idCupon) throws Exception {
            return ResponseEntity.ok().body(new MensajeDTO<>(false, cuponService.eliminarCupon(idCupon)));
        }

        @GetMapping ("/cupon/listar-cupones")
        public ResponseEntity<MensajeDTO<List<CuponDTO>>> listarCupones() throws Exception {
            return ResponseEntity.ok().body(new MensajeDTO<>(false, cuponService.listarCupones()));
        }
    }