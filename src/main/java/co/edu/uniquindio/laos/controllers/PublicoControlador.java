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

@RestController
@RequestMapping("/api/publico")
@RequiredArgsConstructor
public class PublicoControlador {


    //private final EventoService eventoService;
    private final UsuarioService usuarioService;
    private final QuejaSugerenciaService quejaSugerenciaService;

    @PostMapping("/iniciar-sesion")
    public ResponseEntity<MensajeDTO<TokenDTO>> iniciarSesion(@RequestBody IniciarSesionDTO iniciarSesionDTO) throws Exception{
        return ResponseEntity.ok().body( new MensajeDTO<>(false, usuarioService.iniciarSesion(iniciarSesionDTO)) );
    }

    @PostMapping("/crear-usuario")
    public ResponseEntity<MensajeDTO<String>> crearUsuario(@Valid @RequestBody CrearUsuarioDTO crearUsuarioDTO)throws Exception{
        usuarioService.crearUsuario(crearUsuarioDTO);
        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Usuario registrado correctamente")
        );
    }

    @PostMapping("/recuperar-contrasenia")
    public ResponseEntity<MensajeDTO<String>> recuperarContrasenia(@RequestBody RecuperarContraseniaDTO recuperarContraseniaDTO) throws Exception{
        usuarioService.recuperarContrasenia(recuperarContraseniaDTO);
        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Contraseña recuperada correctamente") );
    }

    @GetMapping("/enviar-codigo-recuperacion")
    public ResponseEntity<MensajeDTO<String>> enviarCodigoRecuperacionCuenta(@RequestParam String correo) throws Exception{
        usuarioService.enviarCodigoRecuperacionCuenta(correo);
        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Si su Correo está registrado con nosotros, su código de recuperacion fue enviado correctamente") );
    }
    @GetMapping("/enviar-codigo-activacion")
    public ResponseEntity<MensajeDTO<String>> enviarCodigoActicacionCuenta(@RequestParam String correo) throws Exception{
        usuarioService.enviarCodigoActivacionCuenta(correo);
        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Si su Correo está registrado con nosotros, su código de activacion fue enviado correctamente") );
    }

    @PostMapping("/activar-cuenta")
    public ResponseEntity<MensajeDTO<String>> activarCuenta(@RequestBody ActivarCuentaDTO activarCuentaDTO) throws Exception{
        usuarioService.activarCuenta(activarCuentaDTO);
        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Cuenta activada correctamente") );
    }

    @GetMapping ("/quejas-sugerencias/listar-tipos")
    public ResponseEntity<MensajeDTO<List<String>>> listarTipos() {
        return ResponseEntity.ok().body(new MensajeDTO<>(false, quejaSugerenciaService.obtenerTipoPqrs()));
    }
    @GetMapping ("/quejas-sugerencias/listar-estados")
    public ResponseEntity<MensajeDTO<List<String>>> listarEstados() {
        return ResponseEntity.ok().body(new MensajeDTO<>(false, quejaSugerenciaService.obtenerEstadoPqrs()));
    }
}
