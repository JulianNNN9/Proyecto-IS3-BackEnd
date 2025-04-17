package co.edu.uniquindio.laos.services.implementation;

        import co.edu.uniquindio.laos.dto.faq.CrearFaqDTO;
        import co.edu.uniquindio.laos.dto.faq.FaqDTO;
        import co.edu.uniquindio.laos.model.Faq;
        import co.edu.uniquindio.laos.repositories.FaqRepo;
        import co.edu.uniquindio.laos.services.interfaces.FaqService;
        import lombok.RequiredArgsConstructor;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;

        import java.util.List;
        import java.util.Optional;
        import java.util.stream.Collectors;

        /**
         * Implementación del servicio de preguntas frecuentes (FAQs) que maneja la lógica de negocio
         * para la gestión de preguntas y respuestas en el sistema.
         *
         * Esta clase se encarga de crear, editar, eliminar y consultar preguntas frecuentes
         * para ayudar a los usuarios con información importante sobre el servicio.
         */
        @Service
        @Transactional
        @RequiredArgsConstructor
        public class FaqServiceImple implements FaqService {

            /**
             * Repositorio para el acceso y persistencia de FAQs en la base de datos
             */
            private final FaqRepo faqRepo;

            /**
             * Recupera todas las preguntas frecuentes del sistema
             * @return Lista de objetos FaqDTO con todas las preguntas y respuestas
             */
            public List<FaqDTO> obtenerTodas() {
                return faqRepo.findAll().stream().map(faq -> new FaqDTO(
                        faq.getId(),
                        faq.getPregunta(),
                        faq.getRespuesta()
                )).collect(Collectors.toList());
            }

            /**
             * Método auxiliar para obtener una FAQ por su identificador
             * @param id Identificador único de la FAQ
             * @return Objeto Faq encontrado
             * @throws Exception Si no existe una FAQ con ese ID
             */
            private Faq obtenerPorId(String id) throws Exception {
                Optional<Faq> optionalFaq = faqRepo.findById(id);

                if (optionalFaq.isPresent()) {
                    return optionalFaq.get();
                } else {
                    throw new Exception("FAQ no encontrada");
                }
            }

            /**
             * Recupera una pregunta frecuente específica por su identificador
             * @param id Identificador único de la FAQ
             * @return Objeto FaqDTO con la información de la pregunta y respuesta
             * @throws Exception Si no existe una FAQ con ese ID
             */
            public FaqDTO obtenerFaqPorId(String id) throws Exception {
                Faq faq = obtenerPorId(id);

                return new FaqDTO(
                        faq.getId(),
                        faq.getPregunta(),
                        faq.getRespuesta()
                );
            }

            /**
             * Crea una nueva pregunta frecuente en el sistema
             * @param crearFaqDTO Datos necesarios para crear la FAQ (pregunta y respuesta)
             * @return Identificador único de la FAQ creada
             */
            public String crearFaq(CrearFaqDTO crearFaqDTO) {
                Faq faq = Faq.builder()
                        .pregunta(crearFaqDTO.pregunta())
                        .respuesta(crearFaqDTO.respuesta())
                        .build();
                return faqRepo.save(faq).getId();
            }

            /**
             * Actualiza la información de una pregunta frecuente existente
             * @param faqDTO Datos actualizados de la FAQ
             * @return Mensaje de confirmación de la actualización
             * @throws Exception Si la FAQ no existe
             */
            public String actualizarFaq(FaqDTO faqDTO) throws Exception {
                Faq faq = obtenerPorId(faqDTO.id());
                faq.setPregunta(faqDTO.pregunta());
                faq.setRespuesta(faqDTO.respuesta());
                faqRepo.save(faq);
                return "Se ha actualizado correctamente.";
            }

            /**
             * Elimina una pregunta frecuente del sistema
             * @param id Identificador único de la FAQ a eliminar
             */
            public void eliminarFaq(String id) {
                faqRepo.deleteById(id);
            }
        }