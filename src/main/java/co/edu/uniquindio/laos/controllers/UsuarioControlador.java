package co.edu.uniquindio.laos.controllers;

import co.edu.uniquindio.laos.dto.MensajeDTO;
import co.edu.uniquindio.laos.dto.cita.CrearCitaDTO;
import co.edu.uniquindio.laos.dto.cita.InformacionCitaDTO;
import co.edu.uniquindio.laos.dto.cita.ReprogramarCitaDTO;
import co.edu.uniquindio.laos.dto.cuenta.CambiarContraseniaDTO;
import co.edu.uniquindio.laos.dto.cuenta.EditarUsuarioDTO;
import co.edu.uniquindio.laos.dto.cuenta.InformacionUsuarioDTO;
import co.edu.uniquindio.laos.dto.cuenta.RecuperarContraseniaDTO;
import co.edu.uniquindio.laos.dto.queja.CrearQuejaDTO;
import co.edu.uniquindio.laos.dto.sugerencias.CrearSugerenciaDTO;
import co.edu.uniquindio.laos.model.Estilista;
import co.edu.uniquindio.laos.model.Queja;
import co.edu.uniquindio.laos.model.Servicio;
import co.edu.uniquindio.laos.services.interfaces.*;
import co.edu.uniquindio.laos.services.interfaces.SugerenciaService;
import co.edu.uniquindio.laos.services.interfaces.UsuarioService;
import co.edu.uniquindio.laos.dto.queja.CrearQuejaDTO;
import co.edu.uniquindio.laos.dto.queja.QuejaDTO;
import co.edu.uniquindio.laos.model.Queja;
import co.edu.uniquindio.laos.services.interfaces.QuejaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para operaciones relacionadas con usuarios.
 * Gestiona funcionalidades para usuarios autenticados como:
 * - Gestión de perfil (edición, eliminación)
 * - Creación y consulta de quejas y sugerencias
 * - Gestión de citas (crear, reprogramar, cancelar, consultar)
 * - Consulta de estilistas y servicios disponibles
 *
 * Requiere autenticación con token JWT para acceder a sus funcionalidades.
 */
@RestController
@RequestMapping("/api/usuario")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UsuarioControlador {

    /**
     * Servicio para la gestión de usuarios y autenticación
     */
    private final UsuarioService usuarioService;

    /**
     * Servicio para la gestión de quejas
     */
    private final QuejaService quejaService;

    /**
     * Servicio para la gestión de sugerencias
     */
    private final SugerenciaService sugerenciaService;

    /**
     * Servicio para la gestión de estilistas
     */
    private final EstilistaService estilistaService;

    /**
     * Servicio para la gestión de servicios ofrecidos
     */
    private final ServicioService servicioService;

    /**
     * Servicio para la gestión de citas
     */
    private final CitasService citasService;


    /**
     * Registra una nueva sugerencia en el sistema
     * @param dto Datos para la creación de la sugerencia
     * @return Mensaje de confirmación de la creación
     */
    @PostMapping("/crear-sugerencia")
    public ResponseEntity<MensajeDTO<String>> crearSugerencia(@RequestBody CrearSugerenciaDTO dto) {
        sugerenciaService.crearSugerencia(dto);
        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Sugerencia creada correctamente"));
    }

    /**
     * Registra una nueva queja en el sistema
     * @param crearQuejaDTO Datos para la creación de la queja
     * @return Mensaje de confirmación con el ID de la queja creada
     * @throws Exception Si hay problemas al registrar la queja
     */
    @PostMapping("/crear-queja")
    public ResponseEntity<MensajeDTO<String>> crearQueja(@RequestBody CrearQuejaDTO crearQuejaDTO) throws Exception {
        String id = quejaService.crearQueja(crearQuejaDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Queja creada correctamente con ID: " + id));
    }

    /**
     * Obtiene todas las quejas asociadas a un cliente específico
     * @param clienteId Identificador único del cliente
     * @return Lista de quejas del cliente
     */
    @GetMapping("/obtener-quejas")
    public ResponseEntity<MensajeDTO<List<Queja>>> obtenerQuejasPorClienteId(@RequestParam String clienteId) {
        List<Queja> quejas = quejaService.obtenerListaQuejasPorClienteId(clienteId);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, quejas));
    }

    /**
     * Actualiza la información personal de un usuario
     * @param editarUsuarioDTO Datos actualizados del usuario
     * @return Mensaje de confirmación de la actualización
     * @throws Exception Si el usuario no existe o hay datos inválidos
     */
    @PutMapping("/editar-usuario")
    public ResponseEntity<MensajeDTO<String>> editarUsuario(@Valid @RequestBody EditarUsuarioDTO editarUsuarioDTO) throws Exception {
        usuarioService.editarUsuario(editarUsuarioDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Usuario editado correctamente"));
    }

    /**
     * Elimina un usuario del sistema
     * @param id Identificador único del usuario a eliminar
     * @return Mensaje de confirmación de la eliminación
     * @throws Exception Si el usuario no existe o no puede ser eliminado
     */
    @DeleteMapping("/eliminar-usuario/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarUsuario(@PathVariable String id) throws Exception {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Usuario eliminado correctamente"));
    }

    /**
     * Obtiene información detallada de un usuario específico
     * @param id Identificador único del usuario
     * @return Datos completos del usuario solicitado
     * @throws Exception Si el usuario no existe
     */
    @GetMapping("/obtener-usuario/{codigo}")
    public ResponseEntity<MensajeDTO<InformacionUsuarioDTO>> obtenerInformacionUsuario(@PathVariable String codigo) throws Exception{
        return ResponseEntity.ok().body( new MensajeDTO<>(false,
                usuarioService.obtenerInformacionUsuario(codigo) ) );
    }

    /**
     * Procesa la solicitud de recuperación de contraseña
     * @param recuperarContraseniaDTO Datos para recuperar contraseña
     * @return Mensaje de confirmación de la recuperación
     * @throws Exception Si el código es inválido o ha expirado
     */
    @PostMapping("/recuperar-contrasenia")
    public ResponseEntity<MensajeDTO<String>> recuperarContrasenia(@Valid @RequestBody RecuperarContraseniaDTO recuperarContraseniaDTO) throws Exception {
        usuarioService.recuperarContrasenia(recuperarContraseniaDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Contraseña recuperada correctamente"));
    }

    /**
     * Permite a un usuario cambiar su contraseña actual
     * @param cambiarContraseniaDTO Datos con contraseña actual y nueva
     * @return Mensaje de confirmación del cambio
     * @throws Exception Si la contraseña actual es incorrecta
     */
    @PutMapping("/cambiar-contrasenia")
    public ResponseEntity<MensajeDTO<String>> cambiarContrasenia(@Valid @RequestBody CambiarContraseniaDTO cambiarContraseniaDTO) throws Exception {
        usuarioService.cambiarContrasenia(cambiarContraseniaDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Contraseña cambiada correctamente"));
    }

    /**
     * Obtiene la lista de todos los estilistas disponibles en el sistema
     * @return Lista de estilistas con su información
     */
    @GetMapping("/obtener-estilistas")
    public ResponseEntity<MensajeDTO<List<Estilista>>> obtenerTodosLosEstilistas() {
        List<Estilista> estilistas = estilistaService.obtenerTodosLosEstilistas();
        return ResponseEntity.ok().body(new MensajeDTO<>(false, estilistas));
    }

    /**
     * Obtiene la lista de todos los servicios ofrecidos por el establecimiento
     * @return Lista de servicios disponibles
     */
    @GetMapping("/obtener-servicios")
    public ResponseEntity<MensajeDTO<List<Servicio>>> obtenerTodosLosServicios() {
        List<Servicio> servicios = servicioService.obtenerTodosLosServicios();
        return ResponseEntity.ok().body(new MensajeDTO<>(false, servicios));
    }

    /**
     * Obtiene las quejas realizadas por un cliente específico
     * @param clienteId Identificador único del cliente
     * @return Lista de quejas asociadas al cliente
     */
    @GetMapping("/quejas/{clienteId}")
    public ResponseEntity<MensajeDTO<List<Queja>>> obtenerListaQuejasPorClienteId(@PathVariable String clienteId) {
        List<Queja> quejas = quejaService.obtenerListaQuejasPorClienteId(clienteId);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, quejas));
    }

    /**
     * Registra una nueva cita en el sistema
     * @param crearCitaDTO Datos para la creación de la cita
     * @return Mensaje de confirmación con el ID de la cita creada
     * @throws Exception Si hay conflictos de horario o datos inválidos
     */
    @PostMapping("/citas/crear")
    public ResponseEntity<MensajeDTO<String>> crearCita(@RequestBody CrearCitaDTO crearCitaDTO) throws Exception {
        String idCita = citasService.crearCita(crearCitaDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Cita creada con éxito. ID: " + idCita));
    }

    /**
     * Cambia la fecha y/u hora de una cita existente
     * @param reprogramarCitaDTO Datos para reprogramar la cita
     * @return Mensaje de confirmación con el ID de la cita reprogramada
     * @throws Exception Si la cita no existe o hay conflictos con el nuevo horario
     */
    @PutMapping("/citas/reprogramar")
    public ResponseEntity<MensajeDTO<String>> reprogramarCita(@RequestBody ReprogramarCitaDTO reprogramarCitaDTO) throws Exception {
        String id = citasService.reprogramarCita(reprogramarCitaDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Cita reprogramada correctamente. ID: " + id));
    }

    /**
     * Cancela una cita programada
     * @param citaId Identificador único de la cita a cancelar
     * @return Mensaje de confirmación con el ID de la cita cancelada
     * @throws Exception Si la cita no existe o no puede ser cancelada
     */
    @PutMapping("/citas/cancelar/{citaId}")
    public ResponseEntity<MensajeDTO<String>> cancelarCita(@PathVariable String citaId) throws Exception {
        String id = citasService.cancelarCita(citaId);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Cita cancelada correctamente. ID: " + id));
    }

    /**
     * Obtiene todas las citas asociadas a un cliente específico
     * @param clienteId Identificador único del cliente
     * @return Lista de citas del cliente
     */
    @GetMapping("/citas/mis-citas/{clienteId}")
    public ResponseEntity<MensajeDTO<List<InformacionCitaDTO>>> obtenerMisCitas(@PathVariable String clienteId) {
        List<InformacionCitaDTO> citas = citasService.obtenerCitasPorClienteId(clienteId);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, citas));
    }

    /**
     * Obtiene información detallada de una cita específica
     * @param citaId Identificador único de la cita
     * @return Datos completos de la cita solicitada
     * @throws Exception Si la cita no existe
     */
    @GetMapping("/citas/{citaId}")
    public ResponseEntity<MensajeDTO<InformacionCitaDTO>> obtenerDetalleCita(@PathVariable String citaId) throws Exception {
        InformacionCitaDTO cita = citasService.obtenerCitaPorId(citaId);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, cita));
    }

    /**
     * Consulta información específica de una cita por su identificador
     * @param citaId Identificador único de la cita
     * @return Datos completos de la cita consultada
     * @throws Exception Si la cita no existe
     */
    @GetMapping("/citas/consultar/{citaId}")
    public ResponseEntity<MensajeDTO<InformacionCitaDTO>> consultarCita(@PathVariable String citaId) throws Exception {
        InformacionCitaDTO cita = citasService.obtenerCitaPorId(citaId);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, cita));
    }
}