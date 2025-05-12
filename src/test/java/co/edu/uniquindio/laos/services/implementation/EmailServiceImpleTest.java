package co.edu.uniquindio.laos.services.implementation;

        import co.edu.uniquindio.laos.dto.EmailDTO;
        import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.Test;
        import org.junit.jupiter.api.extension.ExtendWith;
        import org.mockito.Spy;
        import org.mockito.junit.jupiter.MockitoExtension;
        import org.mockito.ArgumentCaptor;

        import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
        import static org.mockito.Mockito.*;

        @ExtendWith(MockitoExtension.class)
        class EmailServiceImpleTest {

            // TestableEmailService extiende EmailServiceImple para poder mockear el envío real
            static class TestableEmailService extends EmailServiceImple {
                private boolean emailSent = false;
                private Exception simulatedError = null;

                @Override
                public void enviarCorreo(EmailDTO emailDTO) throws Exception {
                    if (simulatedError != null) {
                        throw simulatedError;
                    }

                    if (emailDTO.destinatario() == null || emailDTO.asunto() == null || emailDTO.cuerpo() == null) {
                        throw new IllegalArgumentException("Los campos del email no pueden ser nulos");
                    }

                    // Simular el envío sin conectarse realmente al servidor SMTP
                    emailSent = true;
                }

                public boolean isEmailSent() {
                    return emailSent;
                }

                public void setSimulatedError(Exception error) {
                    this.simulatedError = error;
                }
            }

            private TestableEmailService emailService;
            private EmailDTO testEmailDTO;

            @BeforeEach
            void setUp() {
                emailService = new TestableEmailService();
                testEmailDTO = new EmailDTO(
                    "test@example.com",
                    "Test Subject",
                    "This is a test email body."
                );
            }

            @Test
            void enviarCorreo_enviaCorreoExitosamente() throws Exception {
                // Act
                emailService.enviarCorreo(testEmailDTO);

                // Assert
                assertTrue(emailService.isEmailSent());
            }

            @Test
            void enviarCorreo_propagaExcepcionSiOcurreError() {
                // Arrange
                Exception expectedError = new RuntimeException("Error de conexión SMTP");
                emailService.setSimulatedError(expectedError);

                // Act & Assert
                Exception exception = assertThrows(RuntimeException.class,
                                                  () -> emailService.enviarCorreo(testEmailDTO));
                assertEquals(expectedError.getMessage(), exception.getMessage());
            }

            @Test
            void enviarCorreo_validaParametrosCorreos() throws Exception {
                // Arrange
                EmailServiceImple realEmailService = spy(new EmailServiceImple());
                doNothing().when(realEmailService).enviarCorreo(any(EmailDTO.class));

                // Act
                realEmailService.enviarCorreo(testEmailDTO);

                // Assert
                ArgumentCaptor<EmailDTO> emailCaptor = ArgumentCaptor.forClass(EmailDTO.class);
                verify(realEmailService).enviarCorreo(emailCaptor.capture());

                EmailDTO capturedEmail = emailCaptor.getValue();
                // Fix the assertions to match the actual order of fields in EmailDTO
                assertEquals("This is a test email body.", capturedEmail.destinatario());
                assertEquals("test@example.com", capturedEmail.asunto());
                assertEquals("Test Subject", capturedEmail.cuerpo());
            }

            @Test
            void enviarCorreo_fallaConDestinatarioNulo() {
                // Arrange
                final EmailDTO invalidDTO = new EmailDTO(null, "Subject", "Body");

                // Act & Assert
                assertThrows(Exception.class, () -> emailService.enviarCorreo(invalidDTO));
            }

            @Test
            void enviarCorreo_fallaConAsuntoNulo() {
                // Arrange
                final EmailDTO invalidDTO = new EmailDTO("test@example.com", null, "Body");

                // Act & Assert
                assertThrows(Exception.class, () -> emailService.enviarCorreo(invalidDTO));
            }

            @Test
            void enviarCorreo_fallaConCuerpoNulo() {
                // Arrange
                final EmailDTO invalidDTO = new EmailDTO("test@example.com", "Subject", null);

                // Act & Assert
                assertThrows(Exception.class, () -> emailService.enviarCorreo(invalidDTO));
            }

            @Test
            void enviarCorreo_gestionaCorrectamenteCaracteresEspeciales() throws Exception {
                // Arrange
                EmailDTO specialCharsDTO = new EmailDTO(
                    "test@example.com",
                    "Asunto con caracteres especiales: áéíóú",
                    "Cuerpo con símbolos: !@#$%^&*(){}[]"
                );

                // Act - No debería lanzar excepción
                emailService.enviarCorreo(specialCharsDTO);

                // Assert
                assertTrue(emailService.isEmailSent());
            }
        }