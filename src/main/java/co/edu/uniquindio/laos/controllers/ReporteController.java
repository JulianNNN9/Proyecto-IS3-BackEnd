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

/**
 * Controlador REST para la generación de reportes estadísticos.
 * Proporciona endpoints para obtener información analítica sobre
 * quejas y sugerencias desde diferentes perspectivas:
 * - Agrupadas por tipo
 * - Agrupadas por cliente
 * - Filtradas por cliente específico
 *
 * Requiere autenticación con token JWT para acceder a sus funcionalidades.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/reportes")
@SecurityRequirement(name = "bearerAuth")
public class ReporteController {

    /**
     * Servicio para acceder a los datos de quejas y sugerencias
     */
    private final QuejaSugerenciaService quejaSugerenciaService;

    /**
     * Genera un reporte estadístico agrupando las quejas y sugerencias por tipo
     * @return Lista de objetos con información de cantidad por cada tipo de queja/sugerencia
     */
    @GetMapping("/quejas-por-tipo")
    public ResponseEntity<MensajeDTO<List<QuejasPorTipoDTO>>> obtenerQuejasPorTipo() {
        return ResponseEntity.ok().body(new MensajeDTO<>(false,
                quejaSugerenciaService.obtenerQuejasPorTipo()));
    }

    /**
     * Genera un reporte estadístico que muestra el número de quejas y sugerencias
     * registradas por cada cliente del sistema
     * @return Lista de objetos con información de cantidad de quejas por cliente
     */
    @GetMapping("/quejas-por-cliente")
    public ResponseEntity<MensajeDTO<List<QuejasPorClienteDTO>>> obtenerQuejasPorCliente() {
        return ResponseEntity.ok().body(new MensajeDTO<>(false,
                quejaSugerenciaService.obtenerQuejasPorCliente()));
    }

    /**
     * Genera un reporte detallado para un cliente específico, agrupando
     * sus quejas y sugerencias por tipo
     * @param cliente Identificador único del cliente
     * @return Lista de objetos con información de quejas por tipo para el cliente especificado
     */
    @GetMapping("/quejas-por-cliente/{cliente}")
    public ResponseEntity<MensajeDTO<List<QuejasPorTipoDTO>>> obtenerQuejasPorClienteYTipo(@PathVariable String cliente) {
        return ResponseEntity.ok().body(new MensajeDTO<>(false,
                quejaSugerenciaService.obtenerQuejasPorClienteYTipo(cliente)));
    }
}