package co.edu.uniquindio.laos.controllers;

import java.util.List;
import co.edu.uniquindio.laos.dto.MensajeDTO;
import co.edu.uniquindio.laos.dto.TokenDTO;
import co.edu.uniquindio.laos.dto.cuenta.ActivarCuentaDTO;
import co.edu.uniquindio.laos.dto.cuenta.CrearUsuarioDTO;
import co.edu.uniquindio.laos.dto.cuenta.IniciarSesionDTO;
import co.edu.uniquindio.laos.dto.cuenta.RecuperarContraseniaDTO;
import co.edu.uniquindio.laos.services.interfaces.QuejaSugerenciaService;
import co.edu.uniquindio.laos.services.interfaces.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para operaciones públicas del sistema.
 * Gestiona funcionalidades accesibles sin autenticación como:
 * - Registro de usuarios
 * - Inicio de sesión
 * - Recuperación de contraseña
 * - Activación de cuentas
 * - Listado de tipos y estados de quejas y sugerencias
 */
@RestController
@RequestMapping("/api/publico")
@RequiredArgsConstructor
public class PublicoControlador {

    /**
     * Servicio para la gestión de usuarios y autenticación
     */
    private final UsuarioService usuarioService;

    /**
     * Servicio para la gestión de quejas y sugerencias
     */
    private final QuejaSugerenciaService quejaSugerenciaService;

    /**
     * Autentica a un usuario en el sistema y genera un token JWT
     * @param iniciarSesionDTO Datos de inicio de sesión (correo y contraseña)
     * @return Token JWT para autorización en endpoints protegidos
     * @throws Exception Si las credenciales son inválidas o la cuenta está inactiva
     */
    @PostMapping("/iniciar-sesion")
    public ResponseEntity<MensajeDTO<TokenDTO>> iniciarSesion(@RequestBody IniciarSesionDTO iniciarSesionDTO) throws Exception{
        return ResponseEntity.ok().body( new MensajeDTO<>(false, usuarioService.iniciarSesion(iniciarSesionDTO)) );
    }

    /**
     * Registra un nuevo usuario en el sistema
     * @param crearUsuarioDTO Datos completos para la creación del usuario
     * @return Mensaje de confirmación del registro
     * @throws Exception Si el correo ya está registrado o hay datos inválidos
     */
    @PostMapping("/crear-usuario")
    public ResponseEntity<MensajeDTO<String>> crearUsuario(@Valid @RequestBody CrearUsuarioDTO crearUsuarioDTO)throws Exception{
        usuarioService.crearUsuario(crearUsuarioDTO);
        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Usuario registrado correctamente")
        );
    }

    /**
     * Procesa la solicitud de recuperación de contraseña
     * @param recuperarContraseniaDTO Datos para recuperar contraseña (correo y código de verificación)
     * @return Mensaje de confirmación de la recuperación
     * @throws Exception Si el código es inválido o ha expirado
     */
    @PostMapping("/recuperar-contrasenia")
    public ResponseEntity<MensajeDTO<String>> recuperarContrasenia(@RequestBody RecuperarContraseniaDTO recuperarContraseniaDTO) throws Exception{
        usuarioService.recuperarContrasenia(recuperarContraseniaDTO);
        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Contraseña recuperada correctamente") );
    }

    /**
     * Envía un código de recuperación al correo del usuario
     * @param correo Dirección de correo electrónico del usuario
     * @return Mensaje de confirmación del envío
     * @throws Exception Si hay problemas con el envío del correo
     */
    @GetMapping("/enviar-codigo-recuperacion")
    public ResponseEntity<MensajeDTO<String>> enviarCodigoRecuperacionCuenta(@RequestParam String correo) throws Exception{
        usuarioService.enviarCodigoRecuperacionCuenta(correo);
        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Si su Correo está registrado con nosotros, su código de recuperacion fue enviado correctamente") );
    }

    /**
     * Envía un código de activación al correo del usuario
     * @param correo Dirección de correo electrónico del usuario
     * @return Mensaje de confirmación del envío
     * @throws Exception Si hay problemas con el envío del correo
     */
    @GetMapping("/enviar-codigo-activacion")
    public ResponseEntity<MensajeDTO<String>> enviarCodigoActicacionCuenta(@RequestParam String correo) throws Exception{
        usuarioService.enviarCodigoActivacionCuenta(correo);
        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Si su Correo está registrado con nosotros, su código de activacion fue enviado correctamente") );
    }

    /**
     * Activa una cuenta de usuario mediante el código de verificación
     * @param activarCuentaDTO Datos para activar la cuenta (correo y código de activación)
     * @return Mensaje de confirmación de la activación
     * @throws Exception Si el código es inválido o ha expirado
     */
    @PostMapping("/activar-cuenta")
    public ResponseEntity<MensajeDTO<String>> activarCuenta(@RequestBody ActivarCuentaDTO activarCuentaDTO) throws Exception{
        usuarioService.activarCuenta(activarCuentaDTO);
        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Cuenta activada correctamente") );
    }

    /**
     * Obtiene la lista de tipos disponibles para quejas y sugerencias
     * @return Lista de tipos de PQRS (Preguntas, Quejas, Reclamos y Sugerencias)
     */
    @GetMapping ("/quejas-sugerencias/listar-tipos")
    public ResponseEntity<MensajeDTO<List<String>>> listarTipos() {
        return ResponseEntity.ok().body(new MensajeDTO<>(false, quejaSugerenciaService.obtenerTipoPqrs()));
    }

    /**
     * Obtiene la lista de estados posibles para quejas y sugerencias
     * @return Lista de estados de PQRS (ej: pendiente, en proceso, resuelto)
     */
    @GetMapping ("/quejas-sugerencias/listar-estados")
    public ResponseEntity<MensajeDTO<List<String>>> listarEstados() {
        return ResponseEntity.ok().body(new MensajeDTO<>(false, quejaSugerenciaService.obtenerEstadoPqrs()));
    }
}