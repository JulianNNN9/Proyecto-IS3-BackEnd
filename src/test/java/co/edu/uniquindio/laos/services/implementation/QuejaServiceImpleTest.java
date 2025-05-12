package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.dto.queja.CrearQuejaDTO;
import co.edu.uniquindio.laos.exceptions.RecursoNoEncontradoException;
import co.edu.uniquindio.laos.model.EstadoQueja;
import co.edu.uniquindio.laos.model.Queja;
import co.edu.uniquindio.laos.model.RespuestaQueja;
import co.edu.uniquindio.laos.repositories.QuejaRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuejaServiceImpleTest {

    @Mock
    private QuejaRepo quejaRepo;

    @InjectMocks
    private QuejaServiceImple quejaService;

    private Queja quejaSinResponder;
    private Queja quejaRespondida;
    private Queja quejaEliminada;
    private CrearQuejaDTO crearQuejaDTO;
    private LocalDateTime fechaQueja;

    @BeforeEach
    void setUp() {
        fechaQueja = LocalDateTime.now();

        // Crear queja sin responder
        quejaSinResponder = Queja.builder()
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

        // Crear queja respondida
        RespuestaQueja respuesta = new RespuestaQueja("Lamentamos la experiencia", fechaQueja.plusDays(1));
        quejaRespondida = Queja.builder()
                .id("2")
                .clienteId("C002")
                .nombreCliente("Ana Gómez")
                .descripcion("Retraso en la atención")
                .fecha(fechaQueja.minusDays(2))
                .estadoQueja(EstadoQueja.RESPONDIDA)
                .nombreServicio("Manicure")
                .nombreEstilista("Carlos Ruiz")
                .respuestaQueja(respuesta)
                .build();

        // Crear queja eliminada
        quejaEliminada = Queja.builder()
                .id("3")
                .clienteId("C001")
                .nombreCliente("Juan Pérez")
                .descripcion("Queja eliminada")
                .fecha(fechaQueja.minusDays(5))
                .estadoQueja(EstadoQueja.ELIMINADA)
                .nombreServicio("Peinado")
                .nombreEstilista("Laura Torres")
                .respuestaQueja(null)
                .build();

        // DTO para crear queja
        crearQuejaDTO = new CrearQuejaDTO(
                "C001",
                "Juan Pérez",
                "Nueva queja de prueba",
                fechaQueja,
                "Tintura",
                "Pedro Sánchez"
        );
    }

    @Test
    void crearQueja_GuardaYRetorna() throws Exception {
        // Arrange
        Queja nuevaQueja = Queja.builder()
                .id("4")
                .clienteId(crearQuejaDTO.clienteId())
                .nombreCliente(crearQuejaDTO.nombreCliente())
                .descripcion(crearQuejaDTO.descripcion())
                .fecha(crearQuejaDTO.fecha())
                .estadoQueja(EstadoQueja.SIN_RESPONDER)
                .nombreServicio(crearQuejaDTO.nombreServicio())
                .nombreEstilista(crearQuejaDTO.nombreEstilista())
                .respuestaQueja(null)
                .build();

        when(quejaRepo.save(any(Queja.class))).thenReturn(nuevaQueja);

        // Act
        String resultado = quejaService.crearQueja(crearQuejaDTO);

        // Assert
        assertEquals("4", resultado);

        ArgumentCaptor<Queja> quejaCaptor = ArgumentCaptor.forClass(Queja.class);
        verify(quejaRepo).save(quejaCaptor.capture());

        Queja quejaCaptured = quejaCaptor.getValue();
        assertEquals(crearQuejaDTO.clienteId(), quejaCaptured.getClienteId());
        assertEquals(crearQuejaDTO.nombreCliente(), quejaCaptured.getNombreCliente());
        assertEquals(crearQuejaDTO.descripcion(), quejaCaptured.getDescripcion());
        assertEquals(EstadoQueja.SIN_RESPONDER, quejaCaptured.getEstadoQueja());
    }

    @Test
    void eliminarQueja_CambiaEstadoAEliminada() throws RecursoNoEncontradoException {
        // Arrange
        when(quejaRepo.findById("1")).thenReturn(Optional.of(quejaSinResponder));

        // Act
        String resultado = quejaService.eliminarQueja("1");

        // Assert
        assertEquals("1", resultado);
        assertEquals(EstadoQueja.ELIMINADA, quejaSinResponder.getEstadoQueja());
    }

    @Test
    void eliminarQueja_LanzaExcepcionCuandoQuejaNoExiste() {
        // Arrange
        when(quejaRepo.findById("999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> quejaService.eliminarQueja("999"));
        verify(quejaRepo).findById("999");
    }

    @Test
    void eliminarQueja_LanzaExcepcionCuandoQuejaRespondida() {
        // Arrange
        when(quejaRepo.findById("2")).thenReturn(Optional.of(quejaRespondida));

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> quejaService.eliminarQueja("2"));
        verify(quejaRepo).findById("2");
        assertEquals(EstadoQueja.RESPONDIDA, quejaRespondida.getEstadoQueja());
    }

    @Test
    void eliminarQueja_LanzaExcepcionCuandoQuejaYaEliminada() {
        // Arrange
        when(quejaRepo.findById("3")).thenReturn(Optional.of(quejaEliminada));

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> quejaService.eliminarQueja("3"));
        verify(quejaRepo).findById("3");
        assertEquals(EstadoQueja.ELIMINADA, quejaEliminada.getEstadoQueja());
    }

    @Test
    void obtenerQuejaPorId_RetornaQuejaCuandoExiste() throws RecursoNoEncontradoException {
        // Arrange
        when(quejaRepo.findById("1")).thenReturn(Optional.of(quejaSinResponder));

        // Act
        Queja resultado = quejaService.obtenerQuejaPorId("1");

        // Assert
        assertNotNull(resultado);
        assertEquals(quejaSinResponder.getId(), resultado.getId());
        assertEquals(quejaSinResponder.getDescripcion(), resultado.getDescripcion());
    }

    @Test
    void obtenerQuejaPorId_LanzaExcepcionCuandoNoExiste() {
        // Arrange
        when(quejaRepo.findById("999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> quejaService.obtenerQuejaPorId("999"));
    }

    @Test
    void obtenerQuejaPorId_LanzaExcepcionCuandoEstaEliminada() {
        // Arrange
        when(quejaRepo.findById("3")).thenReturn(Optional.of(quejaEliminada));

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> quejaService.obtenerQuejaPorId("3"));
    }

    @Test
    void obtenerListaQuejasPorServicioId_RetornaListaFiltrada() {
        // Arrange
        List<Queja> quejasList = Arrays.asList(quejaSinResponder, quejaRespondida);
        when(quejaRepo.findByServicioId("S001")).thenReturn(quejasList);

        // Act
        List<Queja> resultado = quejaService.obtenerListaQuejasPorServicioId("S001");

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(quejaRepo).findByServicioId("S001");
    }

    @Test
    void obtenerListaQuejasPorClienteId_RetornaListaFiltrada() {
        // Arrange
        List<Queja> quejasList = Arrays.asList(quejaSinResponder, quejaEliminada);
        when(quejaRepo.findByClienteId("C001")).thenReturn(quejasList);

        // Act
        List<Queja> resultado = quejaService.obtenerListaQuejasPorClienteId("C001");

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(quejaRepo).findByClienteId("C001");
    }

    @Test
    void obtenerListaQuejasPorEstado_RetornaListaFiltrada() {
        // Arrange
        List<Queja> quejasList = Collections.singletonList(quejaSinResponder);
        when(quejaRepo.findByEstadoQueja(String.valueOf(EstadoQueja.SIN_RESPONDER))).thenReturn(quejasList);

        // Act
        List<Queja> resultado = quejaService.obtenerListaQuejasPorEstado(EstadoQueja.SIN_RESPONDER);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(EstadoQueja.SIN_RESPONDER, resultado.get(0).getEstadoQueja());
        verify(quejaRepo).findByEstadoQueja(String.valueOf(EstadoQueja.SIN_RESPONDER));
    }

    @Test
    void obtenerListaQuejasPorFecha_RetornaListaEnRangoDeFechas() {
        // Arrange
        LocalDateTime inicio = fechaQueja.minusDays(3);
        LocalDateTime fin = fechaQueja.plusDays(1);
        List<Queja> quejasList = Arrays.asList(quejaSinResponder, quejaRespondida);
        when(quejaRepo.findByFechaBetween(inicio, fin)).thenReturn(quejasList);

        // Act
        List<Queja> resultado = quejaService.obtenerListaQuejasPorFecha(inicio, fin);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(quejaRepo).findByFechaBetween(inicio, fin);
    }

    @Test
    void obtenerListaQuejasPorFechaUnica_RetornaListaDeUnaFecha() {
        // Arrange
        List<Queja> quejasList = Collections.singletonList(quejaSinResponder);
        when(quejaRepo.findByFecha(fechaQueja)).thenReturn(quejasList);

        // Act
        List<Queja> resultado = quejaService.obtenerListaQuejasPorFechaUnica(fechaQueja);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(quejaRepo).findByFecha(fechaQueja);
    }

    @Test
    void listarQuejas_RetornaTodas() {
        // Arrange
        List<Queja> quejasList = Arrays.asList(quejaSinResponder, quejaRespondida, quejaEliminada);
        when(quejaRepo.findAll()).thenReturn(quejasList);

        // Act
        List<Queja> resultado = quejaService.listarQuejas();

        // Assert
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        verify(quejaRepo).findAll();
    }

    @Test
    void responderQueja_GuardaRespuestaYCambiaEstado() throws RecursoNoEncontradoException {
        // Arrange
        when(quejaRepo.findById("1")).thenReturn(Optional.of(quejaSinResponder));

        // Act
        quejaService.responderQueja("1", "Respuesta a la queja");

        // Assert
        assertEquals(EstadoQueja.RESPONDIDA, quejaSinResponder.getEstadoQueja());
        assertNotNull(quejaSinResponder.getRespuestaQueja());
        assertEquals("Respuesta a la queja", quejaSinResponder.getRespuestaQueja().getRespuesta());

        verify(quejaRepo).findById("1");
        verify(quejaRepo).save(quejaSinResponder);
    }

    @Test
    void responderQueja_LanzaExcepcionCuandoQuejaNoExiste() {
        // Arrange
        when(quejaRepo.findById("999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class,
            () -> quejaService.responderQueja("999", "Respuesta"));
        verify(quejaRepo).findById("999");
        verify(quejaRepo, never()).save(any());
    }
}