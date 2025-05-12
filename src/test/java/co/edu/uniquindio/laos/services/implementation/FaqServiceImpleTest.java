package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.dto.faq.CrearFaqDTO;
import co.edu.uniquindio.laos.dto.faq.FaqDTO;
import co.edu.uniquindio.laos.model.Faq;
import co.edu.uniquindio.laos.repositories.FaqRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FaqServiceImpleTest {

    @Mock
    private FaqRepo faqRepo;

    @InjectMocks
    private FaqServiceImple faqService;

    private Faq faq1;
    private Faq faq2;
    private List<Faq> faqList;
    private FaqDTO faqDTO;
    private CrearFaqDTO crearFaqDTO;

    @BeforeEach
    void setUp() {
        // Create sample FAQs
        faq1 = Faq.builder()
                .id("1")
                .pregunta("¿Cómo reservo una cita?")
                .respuesta("Puede reservar a través de nuestra app o llamando.")
                .build();

        faq2 = Faq.builder()
                .id("2")
                .pregunta("¿Cuáles son los horarios de atención?")
                .respuesta("Lunes a viernes de 8am a 6pm.")
                .build();

        faqList = Arrays.asList(faq1, faq2);

        faqDTO = new FaqDTO("1", "¿Pregunta actualizada?", "Respuesta actualizada");
        crearFaqDTO = new CrearFaqDTO("¿Nueva pregunta?", "Nueva respuesta");
    }

    @Test
    void obtenerTodas_RetornaListaConFaqs() {
        // Arrange
        when(faqRepo.findAll()).thenReturn(faqList);

        // Act
        List<FaqDTO> resultado = faqService.obtenerTodas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(faq1.getPregunta(), resultado.get(0).pregunta());
        assertEquals(faq2.getRespuesta(), resultado.get(1).respuesta());
        verify(faqRepo, times(1)).findAll();
    }

    @Test
    void obtenerTodas_RetornaListaVaciaCuandoNoHayFaqs() {
        // Arrange
        when(faqRepo.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<FaqDTO> resultado = faqService.obtenerTodas();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(faqRepo, times(1)).findAll();
    }

    @Test
    void obtenerFaqPorId_RetornaFaqCuandoExiste() throws Exception {
        // Arrange
        when(faqRepo.findById("1")).thenReturn(Optional.of(faq1));

        // Act
        FaqDTO resultado = faqService.obtenerFaqPorId("1");

        // Assert
        assertNotNull(resultado);
        assertEquals(faq1.getId(), resultado.id());
        assertEquals(faq1.getPregunta(), resultado.pregunta());
        assertEquals(faq1.getRespuesta(), resultado.respuesta());
        verify(faqRepo, times(1)).findById("1");
    }

    @Test
    void obtenerFaqPorId_LanzaExcepcionCuandoNoExiste() {
        // Arrange
        when(faqRepo.findById("999")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> faqService.obtenerFaqPorId("999"));
        assertEquals("FAQ no encontrada", exception.getMessage());
        verify(faqRepo, times(1)).findById("999");
    }

    @Test
    void crearFaq_GuardaYRetornaId() {
        // Arrange
        Faq nuevaFaq = Faq.builder()
                .pregunta(crearFaqDTO.pregunta())
                .respuesta(crearFaqDTO.respuesta())
                .build();

        Faq faqGuardada = Faq.builder()
                .id("3")
                .pregunta(crearFaqDTO.pregunta())
                .respuesta(crearFaqDTO.respuesta())
                .build();

        when(faqRepo.save(any(Faq.class))).thenReturn(faqGuardada);

        // Act
        String resultado = faqService.crearFaq(crearFaqDTO);

        // Assert
        assertEquals("3", resultado);

        ArgumentCaptor<Faq> faqCaptor = ArgumentCaptor.forClass(Faq.class);
        verify(faqRepo).save(faqCaptor.capture());

        Faq faqCapturada = faqCaptor.getValue();
        assertEquals(crearFaqDTO.pregunta(), faqCapturada.getPregunta());
        assertEquals(crearFaqDTO.respuesta(), faqCapturada.getRespuesta());
    }

    @Test
    void actualizarFaq_ActualizaYRetornaMensaje() throws Exception {
        // Arrange
        when(faqRepo.findById("1")).thenReturn(Optional.of(faq1));

        // Act
        String resultado = faqService.actualizarFaq(faqDTO);

        // Assert
        assertEquals("Se ha actualizado correctamente.", resultado);

        ArgumentCaptor<Faq> faqCaptor = ArgumentCaptor.forClass(Faq.class);
        verify(faqRepo).save(faqCaptor.capture());

        Faq faqCapturada = faqCaptor.getValue();
        assertEquals(faqDTO.pregunta(), faqCapturada.getPregunta());
        assertEquals(faqDTO.respuesta(), faqCapturada.getRespuesta());
    }

    @Test
    void actualizarFaq_LanzaExcepcionCuandoNoExiste() {
        // Arrange
        when(faqRepo.findById("999")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
            () -> faqService.actualizarFaq(new FaqDTO("999", "Pregunta", "Respuesta")));
        assertEquals("FAQ no encontrada", exception.getMessage());
        verify(faqRepo, times(1)).findById("999");
        verify(faqRepo, never()).save(any());
    }

    @Test
    void eliminarFaq_LlamaDeleteById() {
        // Act
        faqService.eliminarFaq("1");

        // Assert
        verify(faqRepo).deleteById("1");
    }
}