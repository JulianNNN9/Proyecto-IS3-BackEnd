package co.edu.uniquindio.laos.controllers;

import co.edu.uniquindio.laos.dto.MensajeDTO;
import co.edu.uniquindio.laos.dto.quejasugerencia.EnviarQuejaSugerenciaDTO;
import co.edu.uniquindio.laos.dto.quejasugerencia.QuejaSugerenciaDTO;
import co.edu.uniquindio.laos.services.interfaces.QuejaSugerenciaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de quejas y sugerencias.
 * Proporciona endpoints para enviar, actualizar y consultar
 * quejas y sugerencias de los usuarios del sistema.
 *
 * Requiere autenticación con token JWT para acceder a sus funcionalidades.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/quejas-sugerencias")
@SecurityRequirement(name = "bearerAuth")
public class QuejaSugerenciaController {

    /**
     * Servicio para la gestión de quejas y sugerencias
     */
    private final QuejaSugerenciaService quejaSugerenciaService;

    /**
     * Registra una nueva queja o sugerencia en el sistema
     * @param enviarQuejaSugerenciaDTO Datos necesarios para crear la queja o sugerencia
     * @return Identificador único de la queja o sugerencia creada
     * @throws Exception Si hay problemas al registrar la queja o sugerencia
     */
    @PostMapping("/crear")
    public ResponseEntity<MensajeDTO<String>> enviarQuejaSugerencia(@RequestBody EnviarQuejaSugerenciaDTO enviarQuejaSugerenciaDTO) throws Exception {
        return ResponseEntity.ok().body( new MensajeDTO<>(false,
                quejaSugerenciaService.registrarQuejaSugerencia(enviarQuejaSugerenciaDTO)) );
    }

    /**
     * Actualiza el estado y agrega una respuesta a una queja o sugerencia existente
     * @param id Identificador único de la queja o sugerencia
     * @param estado Nuevo estado a asignar (por ejemplo: "REVISADO", "EN_PROCESO", "RESUELTO")
     * @param respuesta Texto de respuesta o comentario sobre la actualización
     * @return Mensaje de confirmación de la actualización
     * @throws Exception Si la queja o sugerencia no existe o no puede ser actualizada
     */
    @PutMapping("/obtener/{id}")
    public ResponseEntity<MensajeDTO<String>> actualizarEstado(@PathVariable String id, @RequestParam String estado, @RequestParam String respuesta) throws Exception {
        return ResponseEntity.ok().body( new MensajeDTO<>(false,
                quejaSugerenciaService.actualizarEstado(id, estado, respuesta)) );
    }

    /**
     * Obtiene todas las quejas y sugerencias registradas en el sistema
     * @return Lista completa de quejas y sugerencias
     */
    @GetMapping("/listar-todos")
    public ResponseEntity<MensajeDTO<List<QuejaSugerenciaDTO>>> obtenerQuejasSugerencias() {
        return ResponseEntity.ok().body( new MensajeDTO<>(false,
                quejaSugerenciaService.obtenerTodas()) );
    }
}