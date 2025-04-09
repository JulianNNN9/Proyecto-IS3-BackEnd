package co.edu.uniquindio.laos.controllers;

import co.edu.uniquindio.laos.dto.MensajeDTO;
import co.edu.uniquindio.laos.dto.cita.InformacionCitaDTO;
import co.edu.uniquindio.laos.services.interfaces.CitasService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estilista")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class EstilistaControlador {

    private final CitasService citasService;

    @GetMapping("/citas/mis-citas/{estilistaId}")
    public ResponseEntity<MensajeDTO<List<InformacionCitaDTO>>> obtenerMisCitas(@PathVariable String estilistaId) {
        List<InformacionCitaDTO> citas = citasService.obtenerCitasPorEstilistaId(estilistaId);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, citas));
    }

    @GetMapping("/citas/{citaId}")
    public ResponseEntity<MensajeDTO<InformacionCitaDTO>> obtenerDetalleCita(@PathVariable String citaId) throws Exception {
        InformacionCitaDTO cita = citasService.obtenerCitaPorId(citaId);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, cita));
    }

    @GetMapping("/citas/estado/{estado}")
    public ResponseEntity<MensajeDTO<List<InformacionCitaDTO>>> obtenerCitasPorEstado(@PathVariable String estado) {
        List<InformacionCitaDTO> citas = citasService.obtenerCitasPorEstado(estado);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, citas));
    }
}