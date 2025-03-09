package co.edu.uniquindio.laos.controllers;

import co.edu.uniquindio.laos.dto.MensajeDTO;
import co.edu.uniquindio.laos.dto.sugerencias.SugerenciaDTO;
import co.edu.uniquindio.laos.services.interfaces.SugerenciaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AdminControlador {

    private final SugerenciaService sugerenciaService;

    @GetMapping("/sugerencias")
    public ResponseEntity<MensajeDTO<List<SugerenciaDTO>>> obtenerSugerencias() {
        return ResponseEntity.ok().body(new MensajeDTO<>(false, sugerenciaService.obtenerSugerencias()));
    }

    @GetMapping("/sugerencias/filtrar")
    public ResponseEntity<MensajeDTO<List<SugerenciaDTO>>> obtenerSugerenciasPorFecha(@RequestParam String fecha) {
        return ResponseEntity.ok().body(new MensajeDTO<>(false, sugerenciaService.obtenerSugerenciasPorFecha(fecha)));
    }

    @PutMapping("/sugerencias/marcar-revisado")
    public ResponseEntity<MensajeDTO<String>> marcarComoRevisado(@RequestBody String id) {
        sugerenciaService.marcarComoRevisado(id);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Sugerencia marcada como revisada correctamente"));
    }
}
