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

@Service
@Transactional
@RequiredArgsConstructor
public class FaqServiceImple implements FaqService {

    private final FaqRepo faqRepo;

    public List<FaqDTO> obtenerTodas() {
        return faqRepo.findAll().stream().map(faq -> new FaqDTO(
                faq.getId(),
                faq.getPregunta(),
                faq.getRespuesta()
        )).collect(Collectors.toList());
    }

    private Faq obtenerPorId(String id) throws Exception {
        Optional<Faq> optionalFaq = faqRepo.findById(id);

        if (optionalFaq.isPresent()) {
            return optionalFaq.get();
        } else {
            throw new Exception("FAQ no encontrada");
        }
    }
    public FaqDTO obtenerFaqPorId(String id) throws Exception {
        Faq faq = obtenerPorId(id);

        return new FaqDTO(
                faq.getId(),
                faq.getPregunta(),
                faq.getRespuesta()
        );
    }

    public String crearFaq(CrearFaqDTO crearFaqDTO) {
        Faq faq = Faq.builder()
                .pregunta(crearFaqDTO.pregunta())
                .respuesta(crearFaqDTO.respuesta())
                .build();
        return faqRepo.save(faq).getId();
    }

    public String actualizarFaq(FaqDTO faqDTO) throws Exception {
        Faq faq = obtenerPorId(faqDTO.id());
        faq.setPregunta(faqDTO.pregunta());
        faq.setRespuesta(faqDTO.respuesta());
        faqRepo.save(faq);
        return "Se ha actualizado correctamente.";
    }

    public void eliminarFaq(String id) {
        faqRepo.deleteById(id);
    }

}
