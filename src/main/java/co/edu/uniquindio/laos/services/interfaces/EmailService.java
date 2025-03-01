package co.edu.uniquindio.laos.services.interfaces;

import co.edu.uniquindio.laos.dto.EmailDTO;

public interface EmailService {

    void enviarCorreo(EmailDTO emailDTO) throws Exception;

}
