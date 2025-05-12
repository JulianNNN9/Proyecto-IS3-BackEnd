package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.dto.cita.CalendarioCitasDTO;
import co.edu.uniquindio.laos.dto.cita.CrearCitaDTO;
import co.edu.uniquindio.laos.dto.cita.InformacionCitaDTO;
import co.edu.uniquindio.laos.dto.cita.ReprogramarCitaDTO;
import co.edu.uniquindio.laos.exceptions.HorarioYEstilistaOcupadoException;
import co.edu.uniquindio.laos.exceptions.RecursoNoEncontradoException;
import co.edu.uniquindio.laos.model.Cita;
import co.edu.uniquindio.laos.model.EstadoCita;
import co.edu.uniquindio.laos.model.Estilista;
import co.edu.uniquindio.laos.model.Servicio;
import co.edu.uniquindio.laos.repositories.CitaRepo;
import co.edu.uniquindio.laos.repositories.EstilistaRepo;
import co.edu.uniquindio.laos.repositories.ServiciosRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitasServiceImpleTest {

    @Mock
    private CitaRepo citaRepo;

    @Mock
    private EstilistaRepo estilistaRepository;

    @Mock
    private ServiciosRepo servicioRepository;

    @InjectMocks
    private CitasServiceImple citasService;

    private Cita citaEjemplo;
    private Estilista estilistaEjemplo;
    private Servicio servicioEjemplo;
    private LocalDateTime fechaHoraEjemplo;

    @BeforeEach
    void setUp() {
        fechaHoraEjemplo = LocalDateTime.of(2023, 10, 15, 14, 30);

        estilistaEjemplo = new Estilista();
        estilistaEjemplo.setId("estilista1");
        estilistaEjemplo.setNombre("Juan Pérez");

        servicioEjemplo = new Servicio();
        servicioEjemplo.setId("servicio1");
        servicioEjemplo.setNombre("Corte de cabello");

        citaEjemplo = Cita.builder()
                .id("cita1")
                .usuarioId("cliente1")
                .estilistaId(estilistaEjemplo.getId())
                .servicioId(servicioEjemplo.getId())
                .fechaHora(fechaHoraEjemplo)
                .estado(EstadoCita.CONFIRMADA)
                .build();
    }

    @Test
    void crearCita_exitoso() throws Exception {
        // Arrange
        CrearCitaDTO crearCitaDTO = new CrearCitaDTO(
                "cliente1",
                estilistaEjemplo.getId(),
                servicioEjemplo.getId(),
                "2023-10-15 14:30"
        );

        when(citaRepo.existsByEstilistaIdAndFechaHora(anyString(), any(LocalDateTime.class))).thenReturn(false);
        when(citaRepo.save(any(Cita.class))).thenAnswer(i -> {
            Cita cita = i.getArgument(0);
            cita.setId("nuevaCitaId");
            return cita;
        });

        // Act
        String resultado = citasService.crearCita(crearCitaDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("nuevaCitaId", resultado);
        verify(citaRepo).save(any(Cita.class));
    }

    @Test
    void crearCita_estilistaOcupado_lanzaExcepcion() {
        // Arrange
        CrearCitaDTO crearCitaDTO = new CrearCitaDTO(
                "cliente1",
                estilistaEjemplo.getId(),
                servicioEjemplo.getId(),
                "2023-10-15 14:30"
        );

        when(citaRepo.existsByEstilistaIdAndFechaHora(anyString(), any(LocalDateTime.class))).thenReturn(true);

        // Act & Assert
        assertThrows(HorarioYEstilistaOcupadoException.class,
                () -> citasService.crearCita(crearCitaDTO));
        verify(citaRepo, never()).save(any(Cita.class));
    }

    @Test
    void cancelarCita_exitoso() throws Exception {
        // Arrange
        when(citaRepo.findById("cita1")).thenReturn(Optional.of(citaEjemplo));

        // Act
        String resultado = citasService.cancelarCita("cita1");

        // Assert
        assertEquals("cita1", resultado);
        assertEquals(EstadoCita.CANCELADA, citaEjemplo.getEstado());
        verify(citaRepo).save(citaEjemplo);
    }

    @Test
    void cancelarCita_citaNoExiste_lanzaExcepcion() {
        // Arrange
        when(citaRepo.findById("citaInexistente")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> citasService.cancelarCita("citaInexistente"));
        verify(citaRepo, never()).save(any(Cita.class));
    }

    @Test
    void reprogramarCita_exitoso() throws Exception {
        // Arrange
        ReprogramarCitaDTO reprogramarDTO = new ReprogramarCitaDTO(
                "cita1",
                "2023-10-20 15:00"
        );

        when(citaRepo.findById("cita1")).thenReturn(Optional.of(citaEjemplo));
        when(citaRepo.existsByEstilistaIdAndFechaHora(anyString(), any(LocalDateTime.class))).thenReturn(false);

        // Act
        String resultado = citasService.reprogramarCita(reprogramarDTO);

        // Assert
        assertEquals("cita1", resultado);
        assertEquals(EstadoCita.REPROGRAMADA, citaEjemplo.getEstado());
        verify(citaRepo).save(citaEjemplo);
    }

    @Test
    void reprogramarCita_citaNoExiste_lanzaExcepcion() {
        // Arrange
        ReprogramarCitaDTO reprogramarDTO = new ReprogramarCitaDTO(
                "citaInexistente",
                "2023-10-20 15:00"
        );

        when(citaRepo.findById("citaInexistente")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> citasService.reprogramarCita(reprogramarDTO));
        verify(citaRepo, never()).save(any(Cita.class));
    }

    @Test
    void reprogramarCita_estilistaOcupado_lanzaExcepcion() {
        // Arrange
        ReprogramarCitaDTO reprogramarDTO = new ReprogramarCitaDTO(
                "cita1",
                "2023-10-20 15:00"
        );

        when(citaRepo.findById("cita1")).thenReturn(Optional.of(citaEjemplo));
        when(citaRepo.existsByEstilistaIdAndFechaHora(anyString(), any(LocalDateTime.class))).thenReturn(true);

        // Act & Assert
        assertThrows(HorarioYEstilistaOcupadoException.class,
                () -> citasService.reprogramarCita(reprogramarDTO));
        verify(citaRepo, never()).save(any(Cita.class));
    }

    @Test
    void obtenerCitasPorClienteId_retornaCitas() {
        // Arrange
        List<String> estadosPermitidos = Arrays.asList("CONFIRMADA", "REPROGRAMADA");
        when(citaRepo.findByUsuarioIdAndEstadoIn("cliente1", estadosPermitidos))
                .thenReturn(List.of(citaEjemplo));
        when(estilistaRepository.findById(estilistaEjemplo.getId()))
                .thenReturn(Optional.of(estilistaEjemplo));
        when(servicioRepository.findById(servicioEjemplo.getId()))
                .thenReturn(Optional.of(servicioEjemplo));

        // Act
        List<InformacionCitaDTO> resultado = citasService.obtenerCitasPorClienteId("cliente1");

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("cita1", resultado.get(0).citaId());
        assertEquals("Juan Pérez", resultado.get(0).estilistaNombre());
    }

    @Test
    void obtenerCitasCanceladasYCompletadasPorClienteId_retornaCitas() {
        // Arrange
        Cita citaCancelada = Cita.builder()
                .id("cita2")
                .usuarioId("cliente1")
                .estilistaId(estilistaEjemplo.getId())
                .servicioId(servicioEjemplo.getId())
                .fechaHora(fechaHoraEjemplo)
                .estado(EstadoCita.CANCELADA)
                .build();

        List<String> estados = Arrays.asList("CANCELADA", "COMPLETADA");
        when(citaRepo.findByUsuarioIdAndEstadoIn("cliente1", estados))
                .thenReturn(List.of(citaCancelada));
        when(estilistaRepository.findById(estilistaEjemplo.getId()))
                .thenReturn(Optional.of(estilistaEjemplo));
        when(servicioRepository.findById(servicioEjemplo.getId()))
                .thenReturn(Optional.of(servicioEjemplo));

        // Act
        List<InformacionCitaDTO> resultado = citasService.obtenerCitasCanceladasYCompletadasPorClienteId("cliente1");

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("cita2", resultado.get(0).citaId());
        assertEquals(EstadoCita.CANCELADA, resultado.get(0).estado());
    }

    @Test
    void obtenerCitasPorEstilistaId_retornaCitas() {
        // Arrange
        when(citaRepo.findByEstilistaId("estilista1"))
                .thenReturn(List.of(citaEjemplo));
        when(estilistaRepository.findById(estilistaEjemplo.getId()))
                .thenReturn(Optional.of(estilistaEjemplo));
        when(servicioRepository.findById(servicioEjemplo.getId()))
                .thenReturn(Optional.of(servicioEjemplo));

        // Act
        List<InformacionCitaDTO> resultado = citasService.obtenerCitasPorEstilistaId("estilista1");

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("estilista1", resultado.get(0).estilistaId());
    }

    @Test
    void obtenerCitaPorId_existente_retornaCita() throws Exception {
        // Arrange
        when(citaRepo.findById("cita1")).thenReturn(Optional.of(citaEjemplo));
        when(estilistaRepository.findById(estilistaEjemplo.getId()))
                .thenReturn(Optional.of(estilistaEjemplo));
        when(servicioRepository.findById(servicioEjemplo.getId()))
                .thenReturn(Optional.of(servicioEjemplo));

        // Act
        InformacionCitaDTO resultado = citasService.obtenerCitaPorId("cita1");

        // Assert
        assertNotNull(resultado);
        assertEquals("cita1", resultado.citaId());
        assertEquals("cliente1", resultado.usuarioId());
    }

    @Test
    void obtenerCitaPorId_noExistente_lanzaExcepcion() {
        // Arrange
        when(citaRepo.findById("citaInexistente")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> citasService.obtenerCitaPorId("citaInexistente"));
    }

    @Test
    void obtenerCitasPorEstado_estadoValido_retornaCitas() {
        // Arrange
        when(citaRepo.findByEstado(EstadoCita.CONFIRMADA))
                .thenReturn(List.of(citaEjemplo));
        when(estilistaRepository.findById(estilistaEjemplo.getId()))
                .thenReturn(Optional.of(estilistaEjemplo));
        when(servicioRepository.findById(servicioEjemplo.getId()))
                .thenReturn(Optional.of(servicioEjemplo));

        // Act
        List<InformacionCitaDTO> resultado = citasService.obtenerCitasPorEstado("CONFIRMADA");

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(EstadoCita.CONFIRMADA, resultado.get(0).estado());
    }

    @Test
    void obtenerCitasPorEstado_estadoInvalido_retornaListaVacia() {
        // Act
        List<InformacionCitaDTO> resultado = citasService.obtenerCitasPorEstado("ESTADO_INVALIDO");

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerCitasConfirmadasYReprogramadas_retornaCitas() {
        // Arrange
        Cita citaReprogramada = Cita.builder()
                .id("cita2")
                .usuarioId("cliente1")
                .estilistaId("estilista1")
                .servicioId("servicio1")
                .fechaHora(fechaHoraEjemplo.plusDays(1))
                .estado(EstadoCita.REPROGRAMADA)
                .build();

        when(citaRepo.findByEstadoIn(List.of("CONFIRMADA", "REPROGRAMADA")))
                .thenReturn(List.of(citaEjemplo, citaReprogramada));

        // Act
        List<CalendarioCitasDTO> resultado = citasService.obtenerCitasConfirmadasYReprogramadas();

        // Assert
        assertEquals(2, resultado.size());
    }
}