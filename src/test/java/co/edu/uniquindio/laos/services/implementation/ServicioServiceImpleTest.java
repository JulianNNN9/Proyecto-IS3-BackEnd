package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.model.Servicio;
import co.edu.uniquindio.laos.repositories.ServiciosRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioServiceImpleTest {

    @Mock
    private ServiciosRepo serviciosRepo;

    @InjectMocks
    private ServicioServiceImple servicioService;

    private Servicio servicio1;
    private Servicio servicio2;
    private List<Servicio> servicioList;

    @BeforeEach
    void setUp() {
        // Create sample services for testing
        servicio1 = Servicio.builder()
                .id("1")
                .nombre("Corte de cabello")
                .precio(25000)
                .duracionMinutos(30)
                .build();

        servicio2 = Servicio.builder()
                .id("2")
                .nombre("Manicure")
                .precio(18000)
                .duracionMinutos(45)
                .build();

        servicioList = Arrays.asList(servicio1, servicio2);
    }

    @Test
    void obtenerTodosLosServicios_RetornaListaConServicios() {
        // Arrange
        when(serviciosRepo.findAll()).thenReturn(servicioList);

        // Act
        List<Servicio> resultado = servicioService.obtenerTodosLosServicios();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(servicio1.getNombre(), resultado.get(0).getNombre());
        assertEquals(servicio2.getPrecio(), resultado.get(1).getPrecio());
        verify(serviciosRepo, times(1)).findAll();
    }

    @Test
    void obtenerTodosLosServicios_RetornaListaVaciaCuandoNoHayServicios() {
        // Arrange
        when(serviciosRepo.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Servicio> resultado = servicioService.obtenerTodosLosServicios();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(serviciosRepo, times(1)).findAll();
    }
}