package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.dto.sugerencias.CrearSugerenciaDTO;
import co.edu.uniquindio.laos.dto.sugerencias.SugerenciaDTO;
import co.edu.uniquindio.laos.model.Sugerencia;
import co.edu.uniquindio.laos.repositories.SugerenciaRepository;
import co.edu.uniquindio.laos.repositories.UsuarioRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SugerenciaServiceImpleTest {

    @Mock
    private SugerenciaRepository sugerenciaRepository;

    @Mock
    private UsuarioRepo usuarioRepo;

    @InjectMocks
    private SugerenciaServiceImple sugerenciaService;

    private Sugerencia sugerencia1;
    private Sugerencia sugerencia2;
    private List<Sugerencia> sugerenciaList;
    private CrearSugerenciaDTO crearSugerenciaDTO;
    private DateTimeFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String fechaActual = LocalDate.now().format(formatter);

        // Create sample suggestions for testing
        sugerencia1 = new Sugerencia();
        sugerencia1.setId("1");
        sugerencia1.setNombre("Juan Pérez");
        sugerencia1.setEmail("juan@example.com");
        sugerencia1.setMotivo("Mejora en servicio");
        sugerencia1.setMensaje("Sugiero ampliar el horario de atención");
        sugerencia1.setFecha(fechaActual);
        sugerencia1.setRevisado(false);

        sugerencia2 = new Sugerencia();
        sugerencia2.setId("2");
        sugerencia2.setNombre("Ana Gómez");
        sugerencia2.setEmail("ana@example.com");
        sugerencia2.setMotivo("Felicitación");
        sugerencia2.setMensaje("Excelente atención recibida");
        sugerencia2.setFecha(fechaActual);
        sugerencia2.setRevisado(true);

        sugerenciaList = Arrays.asList(sugerencia1, sugerencia2);

        // Create DTO for creating a new suggestion
        crearSugerenciaDTO = new CrearSugerenciaDTO(
                "Carlos López",
                "carlos@example.com",
                "Solicitud",
                "Solicito productos veganos"
        );
    }

    @Test
    void crearSugerencia_GuardaYRetornaId() {
        // Arrange
        Sugerencia nuevaSugerencia = new Sugerencia();
        nuevaSugerencia.setId("3");
        nuevaSugerencia.setNombre(crearSugerenciaDTO.nombre());
        nuevaSugerencia.setEmail(crearSugerenciaDTO.email());
        nuevaSugerencia.setMotivo(crearSugerenciaDTO.motivo());
        nuevaSugerencia.setMensaje(crearSugerenciaDTO.mensaje());
        nuevaSugerencia.setFecha(LocalDate.now().format(formatter));
        nuevaSugerencia.setRevisado(false);

        when(sugerenciaRepository.save(any(Sugerencia.class))).thenReturn(nuevaSugerencia);

        // Act
        String resultado = sugerenciaService.crearSugerencia(crearSugerenciaDTO);

        // Assert
        assertEquals("3", resultado);

        ArgumentCaptor<Sugerencia> sugerenciaCaptor = ArgumentCaptor.forClass(Sugerencia.class);
        verify(sugerenciaRepository).save(sugerenciaCaptor.capture());

        Sugerencia guardada = sugerenciaCaptor.getValue();
        assertEquals(crearSugerenciaDTO.nombre(), guardada.getNombre());
        assertEquals(crearSugerenciaDTO.email(), guardada.getEmail());
        assertEquals(crearSugerenciaDTO.motivo(), guardada.getMotivo());
        assertEquals(crearSugerenciaDTO.mensaje(), guardada.getMensaje());
        assertEquals(LocalDate.now().format(formatter), guardada.getFecha());
        assertFalse(guardada.isRevisado());
    }

    @Test
    void obtenerSugerencias_RetornaListaConSugerencias() {
        // Arrange
        when(sugerenciaRepository.findAll()).thenReturn(sugerenciaList);

        // Act
        List<SugerenciaDTO> resultado = sugerenciaService.obtenerSugerencias();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        assertEquals("1", resultado.get(0).id());
        assertEquals("Juan Pérez", resultado.get(0).nombre());
        assertEquals("juan@example.com", resultado.get(0).email());
        assertEquals("Mejora en servicio", resultado.get(0).motivo());
        assertEquals("Sugiero ampliar el horario de atención", resultado.get(0).mensaje());
        assertFalse(resultado.get(0).revisado());

        assertEquals("2", resultado.get(1).id());
        assertTrue(resultado.get(1).revisado());

        verify(sugerenciaRepository, times(1)).findAll();
    }

    @Test
    void marcarComoRevisado_ActualizaSugerencia() {
        // Arrange
        when(sugerenciaRepository.findById("1")).thenReturn(Optional.of(sugerencia1));

        // Act
        sugerenciaService.marcarComoRevisado("1");

        // Assert
        assertTrue(sugerencia1.isRevisado());
        verify(sugerenciaRepository).findById("1");
        verify(sugerenciaRepository).save(sugerencia1);
    }
}