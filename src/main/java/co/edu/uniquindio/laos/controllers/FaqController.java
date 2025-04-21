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

    /**
     * Controlador REST para la gestión de preguntas frecuentes (FAQs).
     * Proporciona endpoints para crear, leer, actualizar y eliminar FAQs
     * que se muestran a los usuarios del sistema.
     *
     * Requiere autenticación con token JWT para realizar operaciones.
     */
    @RestController
    @RequiredArgsConstructor
    @RequestMapping("/faqs")
    @SecurityRequirement(name = "bearerAuth")
    public class FaqController {

        /**
         * Servicio para la gestión de preguntas frecuentes
         */
        private final FaqService faqService;

        /**
         * Obtiene todas las preguntas frecuentes disponibles en el sistema
         * @return Lista completa de FAQs
         */
        @GetMapping("/listar-todos")
        public ResponseEntity<MensajeDTO<List<FaqDTO>>> obtenerTodas() {
            return ResponseEntity.ok().body(new MensajeDTO<>(false, faqService.obtenerTodas()));
        }

        /**
         * Recupera una pregunta frecuente específica por su identificador
         * @param id Identificador único de la FAQ
         * @return Datos completos de la FAQ solicitada
         * @throws Exception Si la FAQ no existe
         */
        @GetMapping("/obtener/{id}")
        public ResponseEntity<MensajeDTO<FaqDTO>> obtenerPorId(@PathVariable String id) throws Exception {
            return ResponseEntity.ok().body(new MensajeDTO<>(false, faqService.obtenerFaqPorId(id)));
        }

        /**
         * Registra una nueva pregunta frecuente en el sistema
         * @param crearFaqDTO Datos para la creación de la FAQ
         * @return Identificador de la FAQ creada
         */
        @PostMapping("/crear")
        public ResponseEntity<MensajeDTO<String>> crearFaq(@RequestBody CrearFaqDTO crearFaqDTO) {
            return ResponseEntity.ok().body(new MensajeDTO<>(false, faqService.crearFaq(crearFaqDTO)));
        }

        /**
         * Modifica una pregunta frecuente existente
         * @param faqDTO Datos actualizados de la FAQ
         * @return Mensaje de confirmación
         * @throws Exception Si la FAQ no existe o no puede ser actualizada
         */
        @PutMapping("/actualizar")
        public ResponseEntity<MensajeDTO<String>> actualizarFaq(@RequestBody FaqDTO faqDTO) throws Exception {
            return ResponseEntity.ok(new MensajeDTO<>(false, faqService.actualizarFaq(faqDTO)));
        }

        /**
         * Elimina una pregunta frecuente del sistema
         * @param id Identificador único de la FAQ a eliminar
         * @return Mensaje de confirmación
         */
        @DeleteMapping("/eliminar/{id}")
        public ResponseEntity<MensajeDTO<String>> eliminarFaq(@PathVariable String id) {
            faqService.eliminarFaq(id);
            return ResponseEntity.ok().body(new MensajeDTO<>(false, "FAQ eliminada exitosamente"));
        }
    }