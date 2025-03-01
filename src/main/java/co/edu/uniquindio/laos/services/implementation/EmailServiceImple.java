package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.dto.EmailDTO;
import co.edu.uniquindio.laos.services.interfaces.EmailService;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImple implements EmailService {

    @Override
    @Async
    public void enviarCorreo(EmailDTO emailDTO) throws Exception {


        Email email = EmailBuilder.startingBlank()
                .from("CAMBIAR@gmail.com")
                .to(emailDTO.destinatario())
                .withSubject(emailDTO.asunto())
                .withPlainText(emailDTO.cuerpo())
                .buildEmail();


        try (Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "CAMBIAR@gmail.com", "CAMBIAR")
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer()) {


            mailer.sendMail(email);
        }


    }


}
