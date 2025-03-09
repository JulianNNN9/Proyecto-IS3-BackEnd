package co.edu.uniquindio.laos.controllers;

import co.edu.uniquindio.laos.dto.MensajeDTO;
import co.edu.uniquindio.laos.dto.sugerencias.SugerenciaDTO;
import co.edu.uniquindio.laos.services.interfaces.SugerenciaService;
import co.edu.uniquindio.laos.dto.queja.QuejaDTO;
import co.edu.uniquindio.laos.model.EstadoQueja;
import co.edu.uniquindio.laos.model.Queja;
import co.edu.uniquindio.laos.services.interfaces.QuejaService;
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
     private final QuejaService quejaService;

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

    @DeleteMapping("/eliminar-queja/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarQueja(@PathVariable String id) throws Exception {
        quejaService.eliminarQueja(id);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Queja eliminada correctamente"));
    }

    @GetMapping("/queja/{id}")
    public ResponseEntity<MensajeDTO<Queja>> obtenerQuejaPorId(@PathVariable String id) throws Exception {
        Queja queja = quejaService.obtenerQuejaPorId(id);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, queja));
    }

    @GetMapping("/quejas/servicio")
    public ResponseEntity<MensajeDTO<List<Queja>>> obtenerQuejasPorServicioId(@RequestParam String servicioId) {
        List<Queja> quejas = quejaService.obtenerListaQuejasPorServicioId(servicioId);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, quejas));
    }

    @GetMapping("/quejas/cliente")
    public ResponseEntity<MensajeDTO<List<Queja>>> obtenerQuejasPorClienteId(@RequestParam String clienteId) {
        List<Queja> quejas = quejaService.obtenerListaQuejasPorClienteId(clienteId);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, quejas));
    }

    @GetMapping("/quejas/estado")
    public ResponseEntity<MensajeDTO<List<Queja>>> obtenerQuejasPorEstado(@RequestParam String estadoQueja) {
        List<Queja> quejas = quejaService.obtenerListaQuejasPorEstado(EstadoQueja.valueOf(estadoQueja));
        return ResponseEntity.ok().body(new MensajeDTO<>(false, quejas));
    }

    @GetMapping("/quejas/fecha")
    public ResponseEntity<MensajeDTO<List<Queja>>> obtenerQuejasPorFecha(@RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        List<Queja> quejas = quejaService.obtenerListaQuejasPorFecha(startDate, endDate);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, quejas));
    }

    @GetMapping("/quejas/fecha-unica")
    public ResponseEntity<MensajeDTO<List<Queja>>> obtenerQuejasPorFechaUnica(@RequestParam LocalDateTime fecha) {
        List<Queja> quejas = quejaService.obtenerListaQuejasPorFechaUnica(fecha);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, quejas));
    }

    @GetMapping("/quejas")
    public ResponseEntity<MensajeDTO<List<Queja>>> listarQuejas() {
        List<Queja> quejas = quejaService.listarQuejas();
        return ResponseEntity.ok().body(new MensajeDTO<>(false, quejas));
    }
}
