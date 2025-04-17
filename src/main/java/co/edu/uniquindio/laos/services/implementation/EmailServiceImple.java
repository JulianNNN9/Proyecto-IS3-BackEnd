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

                /**
                 * Implementación del servicio de correo electrónico que maneja el envío
                 * de correos de manera asíncrona utilizando la biblioteca Simple Java Mail.
                 *
                 * Esta clase se encarga de configurar y enviar correos electrónicos
                 * a los usuarios del sistema utilizando un servidor SMTP de Gmail.
                 */
                @Service
                public class EmailServiceImple implements EmailService {

                    /**
                     * Envía un correo electrónico de forma asíncrona al destinatario especificado
                     *
                     * La anotación @Async permite que este método se ejecute en un hilo separado,
                     * evitando bloquear la ejecución del hilo principal mientras se procesa el envío.
                     *
                     * @param emailDTO Objeto que contiene los datos necesarios para el correo (destinatario, asunto, cuerpo)
                     * @throws Exception Si ocurre algún error durante el proceso de envío
                     */
                    @Override
                    @Async
                    public void enviarCorreo(EmailDTO emailDTO) throws Exception {

                        // Construye el objeto de correo electrónico con los datos proporcionados
                        Email email = EmailBuilder.startingBlank()
                                .from("notificacionesunieventos@gmail.com")
                                .to(emailDTO.destinatario())
                                .withSubject(emailDTO.asunto())
                                .withPlainText(emailDTO.cuerpo())
                                .buildEmail();

                        // Configuración y envío del correo utilizando el servidor SMTP de Gmail
                        try (Mailer mailer = MailerBuilder
                                .withSMTPServer("smtp.gmail.com", 587, "notificacionesunieventos@gmail.com", "gsbn tmbi jdbt uxin")
                                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                                .withDebugLogging(true)
                                .buildMailer()) {

                            // Realiza el envío del correo electrónico
                            mailer.sendMail(email);
                        }
                    }
                }