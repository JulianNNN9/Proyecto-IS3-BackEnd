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

@RestController
@RequiredArgsConstructor
@RequestMapping("/quejas-sugerencias")
@SecurityRequirement(name = "bearerAuth")
public class QuejaSugerenciaController {

    private final QuejaSugerenciaService quejaSugerenciaService;

    @PostMapping("/crear")
    public ResponseEntity<MensajeDTO<String>> enviarQuejaSugerencia(@RequestBody EnviarQuejaSugerenciaDTO enviarQuejaSugerenciaDTO) throws Exception {
        return ResponseEntity.ok().body( new MensajeDTO<>(false,
                quejaSugerenciaService.registrarQuejaSugerencia(enviarQuejaSugerenciaDTO)) );
    }

    @PutMapping("/obtener/{id}")
    public ResponseEntity<MensajeDTO<String>> actualizarEstado(@PathVariable String id, @RequestParam String estado, @RequestParam String respuesta) throws Exception {
        return ResponseEntity.ok().body( new MensajeDTO<>(false,
                quejaSugerenciaService.actualizarEstado(id, estado, respuesta)) );
    }

    @GetMapping("/listar-todos")
    public ResponseEntity<MensajeDTO<List<QuejaSugerenciaDTO>>> obtenerQuejasSugerencias() {
        return ResponseEntity.ok().body( new MensajeDTO<>(false,
                quejaSugerenciaService.obtenerTodas()) );
    }

}
