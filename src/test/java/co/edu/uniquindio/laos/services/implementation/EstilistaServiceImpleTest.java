package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.model.Estilista;
import co.edu.uniquindio.laos.repositories.EstilistaRepo;
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
class EstilistaServiceImpleTest {

    @Mock
    private EstilistaRepo estilistaRepo;

    @InjectMocks
    private EstilistaServiceImple estilistaService;

    private Estilista estilista1;
    private Estilista estilista2;
    private List<Estilista> estilistaList;

    @BeforeEach
    void setUp() {
        // Create sample estilistas
        estilista1 = new Estilista();
        estilista1.setId("1");
        estilista1.setNombre("Juan Perez");

        estilista2 = new Estilista();
        estilista2.setId("2");
        estilista2.setNombre("Maria Rodriguez");

        estilistaList = Arrays.asList(estilista1, estilista2);
    }

    @Test
    void obtenerTodosLosEstilistas_RetornaListaConEstilistas() {
        // Arrange
        when(estilistaRepo.findAll()).thenReturn(estilistaList);

        // Act
        List<Estilista> resultado = estilistaService.obtenerTodosLosEstilistas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(estilista1.getId(), resultado.get(0).getId());
        assertEquals(estilista2.getNombre(), resultado.get(1).getNombre());
        verify(estilistaRepo, times(1)).findAll();
    }

    @Test
    void obtenerTodosLosEstilistas_RetornaListaVaciaCuandoNoHayEstilistas() {
        // Arrange
        when(estilistaRepo.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Estilista> resultado = estilistaService.obtenerTodosLosEstilistas();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(estilistaRepo, times(1)).findAll();
    }

    @Test
    void obtenerTodosLosEstilistas_VerificarLlamadaRepositorio() {
        // Act
        estilistaService.obtenerTodosLosEstilistas();

        // Assert
        verify(estilistaRepo).findAll();
        verifyNoMoreInteractions(estilistaRepo);
    }
}