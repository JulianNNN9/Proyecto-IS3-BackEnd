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

@RestController
@RequestMapping("/api/usuario")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UsuarioControlador {

    private final UsuarioService usuarioService;
    private final QuejaService quejaService;
    private final SugerenciaService sugerenciaService;
    private final EstilistaService estilistaService;
    private final ServicioService servicioService;
    private final CitasService citasService;


    @PostMapping("/crear-sugerencia")
    public ResponseEntity<MensajeDTO<String>> crearSugerencia(@RequestBody CrearSugerenciaDTO dto) {
        sugerenciaService.crearSugerencia(dto);
        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Sugerencia creada correctamente"));
    }
    @PostMapping("/crear-queja")
    public ResponseEntity<MensajeDTO<String>> crearQueja(@RequestBody CrearQuejaDTO crearQuejaDTO) throws Exception {
        String id = quejaService.crearQueja(crearQuejaDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Queja creada correctamente con ID: " + id));
    }

    @GetMapping("/obtener-quejas")
    public ResponseEntity<MensajeDTO<List<Queja>>> obtenerQuejasPorClienteId(@RequestParam String clienteId) {
        List<Queja> quejas = quejaService.obtenerListaQuejasPorClienteId(clienteId);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, quejas));
    }

    @PutMapping("/editar-usuario")
    public ResponseEntity<MensajeDTO<String>> editarUsuario(@Valid @RequestBody EditarUsuarioDTO editarUsuarioDTO) throws Exception {
        usuarioService.editarUsuario(editarUsuarioDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Usuario editado correctamente"));
    }

    @DeleteMapping("/eliminar-usuario/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarUsuario(@PathVariable String id) throws Exception {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Usuario eliminado correctamente"));
    }

    @GetMapping("/informacion-usuario/{id}")
    public ResponseEntity<InformacionUsuarioDTO> obtenerInformacionUsuario(@PathVariable String id) throws Exception {
        InformacionUsuarioDTO informacionUsuario = usuarioService.obtenerInformacionUsuario(id);
        return ResponseEntity.ok(informacionUsuario);
    }

    @PostMapping("/recuperar-contrasenia")
    public ResponseEntity<MensajeDTO<String>> recuperarContrasenia(@Valid @RequestBody RecuperarContraseniaDTO recuperarContraseniaDTO) throws Exception {
        usuarioService.recuperarContrasenia(recuperarContraseniaDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Contraseña recuperada correctamente"));
    }

    @PutMapping("/cambiar-contrasenia")
    public ResponseEntity<MensajeDTO<String>> cambiarContrasenia(@Valid @RequestBody CambiarContraseniaDTO cambiarContraseniaDTO) throws Exception {
        usuarioService.cambiarContrasenia(cambiarContraseniaDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Contraseña cambiada correctamente"));
    }

    @GetMapping("/obtener-estilistas")
    public ResponseEntity<MensajeDTO<List<Estilista>>> obtenerTodosLosEstilistas() {
        List<Estilista> estilistas = estilistaService.obtenerTodosLosEstilistas();
        return ResponseEntity.ok().body(new MensajeDTO<>(false, estilistas));
    }

    @GetMapping("/obtener-servicios")
    public ResponseEntity<MensajeDTO<List<Servicio>>> obtenerTodosLosServicios() {
        List<Servicio> servicios = servicioService.obtenerTodosLosServicios();
        return ResponseEntity.ok().body(new MensajeDTO<>(false, servicios));
    }

    @GetMapping("/quejas/{clienteId}")
    public ResponseEntity<MensajeDTO<List<Queja>>> obtenerListaQuejasPorClienteId(@PathVariable String clienteId) {
        List<Queja> quejas = quejaService.obtenerListaQuejasPorClienteId(clienteId);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, quejas));
    }

    @PostMapping("/citas/crear")
    public ResponseEntity<MensajeDTO<String>> crearCita(@RequestBody CrearCitaDTO crearCitaDTO) throws Exception {
        String idCita = citasService.crearCita(crearCitaDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Cita creada con éxito. ID: " + idCita));
    }

    @PutMapping("/citas/reprogramar")
    public ResponseEntity<MensajeDTO<String>> reprogramarCita(@RequestBody ReprogramarCitaDTO reprogramarCitaDTO) throws Exception {
        String id = citasService.reprogramarCita(reprogramarCitaDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Cita reprogramada correctamente. ID: " + id));
    }

    @PutMapping("/citas/cancelar/{citaId}")
    public ResponseEntity<MensajeDTO<String>> cancelarCita(@PathVariable String citaId) throws Exception {
        String id = citasService.cancelarCita(citaId);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Cita cancelada correctamente. ID: " + id));
    }

    @GetMapping("/citas/mis-citas/{clienteId}")
    public ResponseEntity<MensajeDTO<List<InformacionCitaDTO>>> obtenerMisCitas(@PathVariable String clienteId) {
        List<InformacionCitaDTO> citas = citasService.obtenerCitasPorClienteId(clienteId);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, citas));
    }

    @GetMapping("/citas/{citaId}")
    public ResponseEntity<MensajeDTO<InformacionCitaDTO>> obtenerDetalleCita(@PathVariable String citaId) throws Exception {
        InformacionCitaDTO cita = citasService.obtenerCitaPorId(citaId);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, cita));
    }

    @GetMapping("/citas/consultar/{citaId}")
    public ResponseEntity<MensajeDTO<InformacionCitaDTO>> consultarCita(@PathVariable String citaId) throws Exception {
        InformacionCitaDTO cita = citasService.obtenerCitaPorId(citaId);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, cita));
    }

}