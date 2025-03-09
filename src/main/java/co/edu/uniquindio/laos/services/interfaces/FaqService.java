package co.edu.uniquindio.laos.services.interfaces;

import co.edu.uniquindio.laos.dto.faq.CrearFaqDTO;
import co.edu.uniquindio.laos.dto.faq.FaqDTO;

import java.util.List;
import java.util.Optional;

public interface FaqService {

    List<FaqDTO> obtenerTodas();

    FaqDTO obtenerFaqPorId(String id) throws Exception;

    String crearFaq(CrearFaqDTO crearFaqDTO);

    String actualizarFaq(FaqDTO faqDTO) throws Exception;

    void eliminarFaq(String id);
}
