package co.edu.uniquindio.laos.controllers;

import co.edu.uniquindio.laos.dto.MensajeDTO;
import co.edu.uniquindio.laos.dto.quejasugerencia.QuejasPorClienteDTO;
import co.edu.uniquindio.laos.dto.quejasugerencia.QuejasPorTipoDTO;
import co.edu.uniquindio.laos.services.interfaces.QuejaSugerenciaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reportes")
@SecurityRequirement(name = "bearerAuth")
public class ReporteController {

    private final QuejaSugerenciaService quejaSugerenciaService;

    // Obtener número de quejas por tipo
    @GetMapping("/quejas-por-tipo")
    public ResponseEntity<MensajeDTO<List<QuejasPorTipoDTO>>>  obtenerQuejasPorTipo() {
        return ResponseEntity.ok().body( new MensajeDTO<>(false,
                quejaSugerenciaService.obtenerQuejasPorTipo()) );
    }

    // Obtener cantidad de quejas por cliente
    @GetMapping("/quejas-por-cliente")
    public ResponseEntity<MensajeDTO<List<QuejasPorClienteDTO>>> obtenerQuejasPorCliente() {
        return ResponseEntity.ok().body( new MensajeDTO<>(false,
                quejaSugerenciaService.obtenerQuejasPorCliente()) );
    }

    // Obtener el número de quejas/sugerencias de un cliente por tipo
    @GetMapping("/quejas-por-cliente/{cliente}")
    public ResponseEntity<MensajeDTO<List<QuejasPorTipoDTO>>>  obtenerQuejasPorClienteYTipo(@PathVariable String cliente) {
        return ResponseEntity.ok().body( new MensajeDTO<>(false,
                quejaSugerenciaService.obtenerQuejasPorClienteYTipo(cliente)) );
    }

}
