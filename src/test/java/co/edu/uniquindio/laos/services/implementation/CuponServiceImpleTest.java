package co.edu.uniquindio.laos.services.implementation;

                import co.edu.uniquindio.laos.dto.cupon.CrearCuponDTO;
                import co.edu.uniquindio.laos.dto.cupon.CuponDTO;
                import co.edu.uniquindio.laos.dto.cupon.EditarCuponDTO;
                import co.edu.uniquindio.laos.exceptions.RecursoEncontradoException;
                import co.edu.uniquindio.laos.exceptions.RecursoNoEncontradoException;
                import co.edu.uniquindio.laos.model.Cupon;
                import co.edu.uniquindio.laos.model.EstadoCupon;
                import co.edu.uniquindio.laos.model.EstadoUsuario;
                import co.edu.uniquindio.laos.model.Usuario;
                import co.edu.uniquindio.laos.repositories.CuponRepo;
                import org.junit.jupiter.api.BeforeEach;
                import org.junit.jupiter.api.Test;
                import org.junit.jupiter.api.extension.ExtendWith;
                import org.mockito.InjectMocks;
                import org.mockito.Mock;
                import org.mockito.junit.jupiter.MockitoExtension;

                import java.time.LocalDate;
                import java.util.Arrays;
                import java.util.List;
                import java.util.Optional;
                import java.util.regex.Pattern;

                import static org.junit.jupiter.api.Assertions.*;
                import static org.mockito.ArgumentMatchers.*;
                import static org.mockito.Mockito.*;

                @ExtendWith(MockitoExtension.class)
                public class CuponServiceImpleTest {

                    @Mock
                    private CuponRepo cuponRepo;

                    @InjectMocks
                    private CuponServiceImple cuponService;

                    private Cupon cuponEjemplo;
                    private CrearCuponDTO crearCuponDTO;
                    private EditarCuponDTO editarCuponDTO;
                    private Usuario usuarioEjemplo;
                    private final String USUARIO_ID = "abc123";

                    @BeforeEach
                    void setUp() {
                        // Crear usuario de ejemplo
                        usuarioEjemplo = new Usuario();
                        usuarioEjemplo.setId(USUARIO_ID);
                        usuarioEjemplo.setNombreCompleto("Usuario Test");
                        usuarioEjemplo.setEstadoUsuario(EstadoUsuario.ACTIVO);

                        cuponEjemplo = Cupon.builder()
                                .id("123456789012345678901234")
                                .codigo("TEST01")
                                .nombre("Cupón de prueba")
                                .porcentajeDescuento(10.0)
                                .estadoCupon(EstadoCupon.ACTIVO)
                                .fechaVencimiento(LocalDate.now().plusMonths(1))
                                .usuario(usuarioEjemplo)
                                .build();

                        crearCuponDTO = new CrearCuponDTO(
                                "TEST01",
                                "Cupón de prueba",
                                10.0,
                                EstadoCupon.ACTIVO,
                                LocalDate.now().plusMonths(1),
                                usuarioEjemplo
                        );

                        editarCuponDTO = new EditarCuponDTO(
                                "123456789012345678901234",
                                "TEST01",
                                "Cupón modificado",
                                15.0,
                                EstadoCupon.ACTIVO,
                                LocalDate.now().plusMonths(2)
                        );
                    }

                    @Test
                    void crearCupon_exitoso() throws Exception {
                        // Arrange
                        when(cuponRepo.findByCodigoAndEstadoNot(anyString(), any(EstadoCupon.class)))
                                .thenReturn(Optional.empty());

                        // Act
                        String resultado = cuponService.crearCupon(crearCuponDTO);

                        // Assert
                        assertEquals("Cupon creado exitosamente", resultado);
                        verify(cuponRepo).save(any(Cupon.class));
                    }

                    @Test
                    void crearCupon_codigoExistente_lanzaException() {
                        // Arrange
                        when(cuponRepo.findByCodigoAndEstadoNot(anyString(), any(EstadoCupon.class)))
                                .thenReturn(Optional.of(cuponEjemplo));

                        // Act & Assert
                        assertThrows(RecursoEncontradoException.class, () -> cuponService.crearCupon(crearCuponDTO));
                        verify(cuponRepo, never()).save(any(Cupon.class));
                    }

                    @Test
                    void editarCupon_exitoso() throws Exception {
                        // Arrange
                        when(cuponRepo.findByIdAndEstadoNot(anyString(), any(EstadoCupon.class)))
                                .thenReturn(Optional.of(cuponEjemplo));

                        // Act
                        String resultado = cuponService.editarCupon(editarCuponDTO);

                        // Assert
                        assertEquals(cuponEjemplo.getId(), resultado);
                        assertEquals("Cupón modificado", cuponEjemplo.getNombre());
                        assertEquals(15.0, cuponEjemplo.getPorcentajeDescuento());
                        verify(cuponRepo).save(cuponEjemplo);
                    }

                    @Test
                    void editarCupon_noExiste_lanzaException() {
                        // Arrange
                        when(cuponRepo.findByIdAndEstadoNot(anyString(), any(EstadoCupon.class)))
                                .thenReturn(Optional.empty());

                        // Act & Assert
                        assertThrows(RecursoNoEncontradoException.class, () -> cuponService.editarCupon(editarCuponDTO));
                    }

                    @Test
                    void eliminarCupon_exitoso() throws Exception {
                        // Arrange
                        when(cuponRepo.findByIdAndEstadoNot(anyString(), any(EstadoCupon.class)))
                                .thenReturn(Optional.of(cuponEjemplo));

                        // Act
                        String resultado = cuponService.eliminarCupon(cuponEjemplo.getId());

                        // Assert
                        assertEquals("Cupón eliminado con éxito.", resultado);
                        assertEquals(EstadoCupon.ELIMINADO, cuponEjemplo.getEstadoCupon());
                        verify(cuponRepo).save(cuponEjemplo);
                    }

                    @Test
                    void eliminarCupon_noExiste_lanzaException() {
                        // Arrange
                        when(cuponRepo.findByIdAndEstadoNot(anyString(), any(EstadoCupon.class)))
                                .thenReturn(Optional.empty());

                        // Act & Assert
                        assertThrows(RecursoNoEncontradoException.class, () -> cuponService.eliminarCupon("idInexistente"));
                    }

                    @Test
                    void obtenerCuponPorCodigo_exitoso() throws Exception {
                        // Arrange
                        when(cuponRepo.findByCodigoAndEstadoNot(anyString(), any(EstadoCupon.class)))
                                .thenReturn(Optional.of(cuponEjemplo));

                        // Act
                        Cupon resultado = cuponService.obtenerCuponPorCodigo("TEST01");

                        // Assert
                        assertNotNull(resultado);
                        assertEquals(cuponEjemplo.getCodigo(), resultado.getCodigo());
                    }

                    @Test
                    void obtenerCuponPorCodigo_noExiste_lanzaException() {
                        // Arrange
                        when(cuponRepo.findByCodigoAndEstadoNot(anyString(), any(EstadoCupon.class)))
                                .thenReturn(Optional.empty());

                        // Act & Assert
                        assertThrows(RecursoNoEncontradoException.class, () -> cuponService.obtenerCuponPorCodigo("NOEXISTE"));
                    }

                    @Test
                    void obtenerListaCuponPorIdUsuario_exitoso() {
                        // Arrange
                        when(cuponRepo.findByUsuarioIdAndEstadoNot(anyString(), any(EstadoCupon.class)))
                                .thenReturn(Arrays.asList(cuponEjemplo));

                        // Act
                        List<Cupon> resultado = cuponService.obtenerListaCuponPorIdUsuario(USUARIO_ID);

                        // Assert
                        assertFalse(resultado.isEmpty());
                        assertEquals(1, resultado.size());
                    }

                    @Test
                    void obtenerListaCuponPorIdUsuario_formateoId() {
                        // Arrange
                        String idCorto = "abc";
                        when(cuponRepo.findByUsuarioIdAndEstadoNot(anyString(), any(EstadoCupon.class)))
                                .thenReturn(Arrays.asList(cuponEjemplo));

                        // Act
                        cuponService.obtenerListaCuponPorIdUsuario(idCorto);

                        // Assert - Verificar que se llama al repo con el ID formateado a 24 caracteres
                        verify(cuponRepo).findByUsuarioIdAndEstadoNot(argThat(arg -> arg.length() == 24), any(EstadoCupon.class));
                    }

                    @Test
                    void obtenerCuponPorCodigoYIdUsuario_exitoso() throws Exception {
                        // Arrange
                        when(cuponRepo.findByCodigoAndIdUsuarioAndEstadoNot(anyString(), anyString(), any(EstadoCupon.class)))
                                .thenReturn(Optional.of(cuponEjemplo));

                        // Act
                        Cupon resultado = cuponService.obtenerCuponPorCodigoYIdUsuario("TEST01", USUARIO_ID);

                        // Assert
                        assertNotNull(resultado);
                        assertEquals(cuponEjemplo.getCodigo(), resultado.getCodigo());
                    }

                    @Test
                    void obtenerCuponPorCodigoYIdUsuario_noExiste_lanzaException() {
                        // Arrange
                        when(cuponRepo.findByCodigoAndIdUsuarioAndEstadoNot(anyString(), anyString(), any(EstadoCupon.class)))
                                .thenReturn(Optional.empty());

                        // Act & Assert
                        assertThrows(RecursoNoEncontradoException.class,
                                () -> cuponService.obtenerCuponPorCodigoYIdUsuario("NOEXISTE", USUARIO_ID));
                    }

                    @Test
                    void obtenerCuponPorId_exitoso() throws Exception {
                        // Arrange
                        when(cuponRepo.findByIdAndEstadoNot(anyString(), any(EstadoCupon.class)))
                                .thenReturn(Optional.of(cuponEjemplo));

                        // Act
                        Cupon resultado = cuponService.obtenerCuponPorId(cuponEjemplo.getId());

                        // Assert
                        assertNotNull(resultado);
                        assertEquals(cuponEjemplo.getId(), resultado.getId());
                    }

                    @Test
                    void obtenerCuponPorId_noExiste_lanzaException() {
                        // Arrange
                        when(cuponRepo.findByIdAndEstadoNot(anyString(), any(EstadoCupon.class)))
                                .thenReturn(Optional.empty());

                        // Act & Assert
                        assertThrows(RecursoNoEncontradoException.class, () -> cuponService.obtenerCuponPorId("idInexistente"));
                    }

                    @Test
                    void listarCupones_exitoso() {
                        // Arrange
                        when(cuponRepo.findAll())
                                .thenReturn(Arrays.asList(cuponEjemplo));

                        // Act
                        List<CuponDTO> resultado = cuponService.listarCupones();

                        // Assert
                        assertFalse(resultado.isEmpty());
                        assertEquals(1, resultado.size());
                        assertEquals(cuponEjemplo.getCodigo(), resultado.get(0).codigo());
                    }

                    @Test
                    void generarCodigoCupon_formatoCorrecto() {
                        // Act
                        String codigo = cuponService.generarCodigoCupon();

                        // Assert
                        assertNotNull(codigo);
                        assertEquals(6, codigo.length());
                        // Verificar que solo contiene caracteres alfanuméricos en mayúscula
                        assertTrue(Pattern.matches("^[A-Z0-9]{6}$", codigo));
                    }
                }