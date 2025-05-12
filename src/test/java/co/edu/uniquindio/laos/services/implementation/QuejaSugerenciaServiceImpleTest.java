package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.exceptions.RecursoNoEncontradoException;
import co.edu.uniquindio.laos.model.EstadoQueja;
import co.edu.uniquindio.laos.model.Queja;
import co.edu.uniquindio.laos.model.RespuestaQueja;
import co.edu.uniquindio.laos.repositories.QuejaRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuejaServiceImpleTest_RespuestaQueja {

    @Mock
    private QuejaRepo quejaRepo;

    @InjectMocks
    private QuejaServiceImple quejaService;

    @Test
    void responderQueja_ActualizaEstadoYGuardaRespuesta() throws RecursoNoEncontradoException {
        // Arrange
        LocalDateTime fechaQueja = LocalDateTime.now();

        Queja queja = Queja.builder()
                .id("1")
                .clienteId("C001")
                .nombreCliente("Juan Pérez")
                .descripcion("Servicio insatisfactorio")
                .fecha(fechaQueja)
                .estadoQueja(EstadoQueja.SIN_RESPONDER)
                .nombreServicio("Corte de cabello")
                .nombreEstilista("María López")
                .respuestaQueja(null)
                .build();

        String textoRespuesta = "Lamentamos lo ocurrido. Tomaremos medidas para mejorar.";

        when(quejaRepo.findById("1")).thenReturn(Optional.of(queja));

        // Act
        quejaService.responderQueja("1", textoRespuesta);

        // Assert
        assertEquals(EstadoQueja.RESPONDIDA, queja.getEstadoQueja());
        assertNotNull(queja.getRespuestaQueja());

        // Verifica el texto de la respuesta usando el getter correcto
        assertEquals(textoRespuesta, queja.getRespuestaQueja().getRespuesta());

        ArgumentCaptor<Queja> quejaCaptor = ArgumentCaptor.forClass(Queja.class);
        verify(quejaRepo).save(quejaCaptor.capture());

        Queja quejaSalvada = quejaCaptor.getValue();
        assertEquals(EstadoQueja.RESPONDIDA, quejaSalvada.getEstadoQueja());
        assertEquals(textoRespuesta, quejaSalvada.getRespuestaQueja().getRespuesta());
    }

    @Test
    void responderQueja_LanzaExcepcionSiNoExisteQueja() {
        // Arrange
        String idQueja = "999";
        when(quejaRepo.findById(idQueja)).thenReturn(Optional.empty());

        // Act & Assert
        RecursoNoEncontradoException exception = assertThrows(
            RecursoNoEncontradoException.class,
            () -> quejaService.responderQueja(idQueja, "Una respuesta")
        );

        assertEquals("Queja no encontrada", exception.getMessage());
        verify(quejaRepo, never()).save(any());
    }
}