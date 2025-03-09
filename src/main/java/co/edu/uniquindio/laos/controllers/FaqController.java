package co.edu.uniquindio.laos.controllers;

import co.edu.uniquindio.laos.dto.MensajeDTO;
import co.edu.uniquindio.laos.dto.faq.CrearFaqDTO;
import co.edu.uniquindio.laos.dto.faq.FaqDTO;
import co.edu.uniquindio.laos.services.interfaces.FaqService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/faqs")
@SecurityRequirement(name = "bearerAuth")
public class FaqController {

    private final FaqService faqService;

    // Obtener todas las FAQs
    @GetMapping("/listar-todos")
    public ResponseEntity<MensajeDTO<List<FaqDTO>>> obtenerTodas() {
        return ResponseEntity.ok().body(new MensajeDTO<>(false, faqService.obtenerTodas()));
    }

    // Obtener una FAQ por ID
    @GetMapping("/obtener/{id}")
    public ResponseEntity<MensajeDTO<FaqDTO>> obtenerPorId(@PathVariable String id) throws Exception {
        return ResponseEntity.ok().body(new MensajeDTO<>(false, faqService.obtenerFaqPorId(id)));
    }

    // Crear una nueva FAQ
    @PostMapping("/crear")
    public ResponseEntity<MensajeDTO<String>> crearFaq(@RequestBody CrearFaqDTO crearFaqDTO) {
        return ResponseEntity.ok().body(new MensajeDTO<>(false, faqService.crearFaq(crearFaqDTO)));
    }

    // Actualizar una FAQ
    @PutMapping("/actualizar")
    public ResponseEntity<MensajeDTO<String>> actualizarFaq(@RequestBody FaqDTO faqDTO) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false, faqService.actualizarFaq(faqDTO)));
    }

    // Eliminar una FAQ
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarFaq(@PathVariable String id) {
        faqService.eliminarFaq(id);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "FAQ eliminada exitosamente"));
    }
}