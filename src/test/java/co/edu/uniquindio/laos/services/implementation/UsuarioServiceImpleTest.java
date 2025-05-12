package co.edu.uniquindio.laos.services.implementation;

                                import co.edu.uniquindio.laos.config.JWTUtils;
                                import co.edu.uniquindio.laos.dto.EmailDTO;
                                import co.edu.uniquindio.laos.dto.TokenDTO;
                                import co.edu.uniquindio.laos.dto.cuenta.*;
                                import co.edu.uniquindio.laos.exceptions.*;
                                import co.edu.uniquindio.laos.model.*;
                                import co.edu.uniquindio.laos.repositories.UsuarioRepo;
                                import co.edu.uniquindio.laos.services.interfaces.CuponService;
                                import co.edu.uniquindio.laos.services.interfaces.EmailService;
                                import org.junit.jupiter.api.BeforeEach;
                                import org.junit.jupiter.api.Test;
                                import org.junit.jupiter.api.extension.ExtendWith;
                                import org.mockito.ArgumentCaptor;
                                import org.mockito.InjectMocks;
                                import org.mockito.Mock;
                                import org.mockito.junit.jupiter.MockitoExtension;
                                import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

                                import java.time.LocalDateTime;
                                import java.util.Map;
                                import java.util.Optional;

                                import static org.junit.jupiter.api.Assertions.*;
                                import static org.mockito.ArgumentMatchers.*;
                                import static org.mockito.Mockito.*;

                                @ExtendWith(MockitoExtension.class)
                                class UsuarioServiceImpleTest {

                                    @Mock
                                    private JWTUtils jwtUtils;

                                    @Mock
                                    private UsuarioRepo usuarioRepo;

                                    @Mock
                                    private CuponService cuponService;

                                    @Mock
                                    private EmailService emailService;

                                    @InjectMocks
                                    private UsuarioServiceImple usuarioService;

                                    private Usuario usuario;
                                    private String passwordEncriptada;
                                    private LocalDateTime fechaActual;

                                    @BeforeEach
                                    void setUp() {
                                        fechaActual = LocalDateTime.now();
                                        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                                        passwordEncriptada = passwordEncoder.encode("Clave123*");

                                        usuario = Usuario.builder()
                                                .id("1")
                                                .cedula("1094123456")
                                                .nombreCompleto("Juan Pérez")
                                                .direccion("Calle 123")
                                                .telefono("3001234567")
                                                .email("juan@example.com")
                                                .contrasenia(passwordEncriptada)
                                                .rol(Rol.CLIENTE)
                                                .estadoUsuario(EstadoUsuario.ACTIVO)
                                                .fechaRegistro(fechaActual.minusDays(30))
                                                .build();
                                    }

                                    @Test
                                    void refreshToken_RetornaNuevoToken() {
                                        // Arrange
                                        String tokenExpirado = "token.expirado.jwt";
                                        String nuevoToken = "nuevo.token.jwt";
                                        when(jwtUtils.refreshToken(tokenExpirado)).thenReturn(nuevoToken);

                                        // Act
                                        TokenDTO resultado = usuarioService.refreshToken(tokenExpirado);

                                        // Assert
                                        assertEquals(nuevoToken, resultado.token());
                                        verify(jwtUtils).refreshToken(tokenExpirado);
                                    }

                                   @Test
                                   void crearUsuario_GuardaYRetornaId() throws Exception {
                                       // Arrange
                                       String randomEmail = "test" + System.currentTimeMillis() + "@example.com";

                                       CrearUsuarioDTO crearUsuarioDTO = new CrearUsuarioDTO(
                                               "1094123456",
                                               "Juan Pérez",
                                               "Calle 123",
                                               "3001234567",
                                               randomEmail,
                                               "Clave123*"
                                       );

                                       // Mock the repository behavior - use simpler, more permissive mocks
                                       when(usuarioRepo.findByCedulaAndEstadoUsuarioNot(anyString(), any())).thenReturn(Optional.empty());
                                       when(usuarioRepo.findByEmailAndEstadoUsuarioNot(anyString(), any())).thenReturn(Optional.empty());

                                       Usuario usuarioCreado = Usuario.builder()
                                               .id("1")
                                               .email(randomEmail)
                                               .build();
                                       when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuarioCreado);

                                       
                                       // Act
                                       String resultado = usuarioService.crearUsuario(crearUsuarioDTO);

                                       // Assert
                                       assertEquals("1", resultado);

                                       ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
                                       verify(usuarioRepo).save(usuarioCaptor.capture());

                                       Usuario guardado = usuarioCaptor.getValue();
                                       assertEquals(crearUsuarioDTO.cedula(), guardado.getCedula());
                                       assertEquals(crearUsuarioDTO.nombreCompleto(), guardado.getNombreCompleto());
                                       assertEquals(EstadoUsuario.INACTIVO, guardado.getEstadoUsuario());

                                   }

                                    @Test
                                    void crearUsuario_LanzaExcepcionSiCedulaExiste() {
                                        // Arrange
                                        CrearUsuarioDTO crearUsuarioDTO = new CrearUsuarioDTO(
                                                "1094123456",
                                                "Juan Pérez",
                                                "Calle 123",
                                                "3001234567",
                                                "juan@example.com",
                                                "Clave123*"
                                        );

                                        when(usuarioRepo.findByCedulaAndEstadoUsuarioNot(anyString(), any())).thenReturn(Optional.of(usuario));

                                        // Act & Assert
                                        RecursoEncontradoException exception = assertThrows(
                                                RecursoEncontradoException.class,
                                                () -> usuarioService.crearUsuario(crearUsuarioDTO)
                                        );

                                        assertEquals("La cedula ya está en uso", exception.getMessage());
                                        verify(usuarioRepo, never()).save(any());
                                    }

                                    @Test
                                    void crearUsuario_LanzaExcepcionSiEmailExiste() {
                                        // Arrange
                                        CrearUsuarioDTO crearUsuarioDTO = new CrearUsuarioDTO(
                                                "1094123456",
                                                "Juan Pérez",
                                                "Calle 123",
                                                "3001234567",
                                                "juan@example.com",
                                                "Clave123*"
                                        );

                                        when(usuarioRepo.findByCedulaAndEstadoUsuarioNot(anyString(), any())).thenReturn(Optional.empty());
                                        when(usuarioRepo.findByEmailAndEstadoUsuarioNot(anyString(), any())).thenReturn(Optional.of(usuario));

                                        // Act & Assert
                                        RecursoEncontradoException exception = assertThrows(
                                                RecursoEncontradoException.class,
                                                () -> usuarioService.crearUsuario(crearUsuarioDTO)
                                        );

                                        assertEquals("Este email ya está en uso", exception.getMessage());
                                        verify(usuarioRepo, never()).save(any());
                                    }

                                    @Test
                                    void editarUsuario_ActualizaInformacion() throws RecursoNoEncontradoException {
                                        // Arrange
                                        EditarUsuarioDTO editarUsuarioDTO = new EditarUsuarioDTO(
                                                "1",
                                                "Juan Antonio Pérez",
                                                "Carrera 45 #23-12",
                                                "3109876543"
                                        );

                                        when(usuarioRepo.findByIdAndEstadoUsuarioNot(anyString(), any())).thenReturn(Optional.of(usuario));

                                        // Act
                                        usuarioService.editarUsuario(editarUsuarioDTO);

                                        // Assert
                                        assertEquals("Juan Antonio Pérez", usuario.getNombreCompleto());
                                        assertEquals("Carrera 45 #23-12", usuario.getDireccion());
                                        assertEquals("3109876543", usuario.getTelefono());
                                        verify(usuarioRepo).save(usuario);
                                    }

                                    @Test
                                    void eliminarUsuario_CambiaEstadoAEliminado() throws RecursoNoEncontradoException {
                                        // Arrange
                                        when(usuarioRepo.findByIdAndEstadoUsuarioNot(anyString(), any())).thenReturn(Optional.of(usuario));

                                        // Act
                                        usuarioService.eliminarUsuario("1");

                                        // Assert
                                        assertEquals(EstadoUsuario.ELIMINADO, usuario.getEstadoUsuario());
                                        verify(usuarioRepo).save(usuario);
                                    }

                                    @Test
                                    void obtenerInformacionUsuario_RetornaDTO() throws RecursoNoEncontradoException {
                                        // Arrange
                                        when(usuarioRepo.findByIdAndEstadoUsuarioNot(anyString(), any())).thenReturn(Optional.of(usuario));

                                        // Act
                                        InformacionUsuarioDTO resultado = usuarioService.obtenerInformacionUsuario("1");

                                        // Assert
                                        assertEquals(usuario.getCedula(), resultado.cedula());
                                        assertEquals(usuario.getNombreCompleto(), resultado.nombreCompleto());
                                        assertEquals(usuario.getDireccion(), resultado.direccion());
                                        assertEquals(usuario.getTelefono(), resultado.telefono());
                                        assertEquals(usuario.getEmail(), resultado.email());
                                    }

                                    @Test
                                    void enviarCodigoRecuperacionCuenta_EnviaCorreo() throws Exception {
                                        // Arrange
                                        when(usuarioRepo.findByEmailAndEstadoUsuarioNot(anyString(), any())).thenReturn(Optional.of(usuario));
                                        doNothing().when(emailService).enviarCorreo(any(EmailDTO.class));

                                        // Act
                                        usuarioService.enviarCodigoRecuperacionCuenta("juan@example.com");

                                        // Assert
                                        verify(usuarioRepo).save(usuario);
                                        verify(emailService).enviarCorreo(any(EmailDTO.class));
                                        assertNotNull(usuario.getCodigoRecuperacion());
                                    }

                                    @Test
                                    void cambiarContrasenia_ActualizaContrasenia() throws RecursoNoEncontradoException,
                                            ContraseniaNoCoincidenException, ContraseniaIncorrectaException {
                                        // Arrange
                                        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                                        String contraseniaPlana = "Clave123*";
                                        String nuevaContrasenia = "NuevaClave456*";

                                        Usuario usuarioConClave = Usuario.builder()
                                                .id("1")
                                                .contrasenia(passwordEncoder.encode(contraseniaPlana))
                                                .build();

                                        CambiarContraseniaDTO cambiarContraseniaDTO = new CambiarContraseniaDTO(
                                                "1",
                                                contraseniaPlana,
                                                nuevaContrasenia,
                                                nuevaContrasenia
                                        );

                                        when(usuarioRepo.findByIdAndEstadoUsuarioNot(anyString(), any())).thenReturn(Optional.of(usuarioConClave));

                                        // Act
                                        usuarioService.cambiarContrasenia(cambiarContraseniaDTO);

                                        // Assert
                                        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
                                        verify(usuarioRepo).save(usuarioCaptor.capture());

                                        Usuario guardado = usuarioCaptor.getValue();
                                        assertTrue(passwordEncoder.matches(nuevaContrasenia, guardado.getContrasenia()));
                                    }

                                    @Test
                                    void cambiarContrasenia_LanzaExcepcionSiContraseniaNoCoincide() throws RecursoNoEncontradoException {
                                        // Arrange
                                        CambiarContraseniaDTO cambiarContraseniaDTO = new CambiarContraseniaDTO(
                                                "1",
                                                "Clave123*",
                                                "Nueva1*",
                                                "Distinta2*"
                                        );

                                        when(usuarioRepo.findByIdAndEstadoUsuarioNot(anyString(), any())).thenReturn(Optional.of(usuario));

                                        // Act & Assert
                                        ContraseniaNoCoincidenException exception = assertThrows(
                                                ContraseniaNoCoincidenException.class,
                                                () -> usuarioService.cambiarContrasenia(cambiarContraseniaDTO)
                                        );

                                        assertEquals("Las contraseñas no coindicen", exception.getMessage());
                                        verify(usuarioRepo, never()).save(any());
                                    }

                                    @Test
                                    void iniciarSesion_GeneraToken() throws RecursoNoEncontradoException, CuentaInactivaEliminadaException,
                                            CuentaBloqueadaException, ContraseniaIncorrectaException {
                                        // Arrange
                                        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                                        String contraseniaPlana = "Clave123*";

                                        Usuario usuarioLogin = Usuario.builder()
                                                .id("1")
                                                .nombreCompleto("Juan Pérez")
                                                .email("juan@example.com")
                                                .contrasenia(passwordEncoder.encode(contraseniaPlana))
                                                .rol(Rol.CLIENTE)
                                                .estadoUsuario(EstadoUsuario.ACTIVO)
                                                .build();

                                        IniciarSesionDTO iniciarSesionDTO = new IniciarSesionDTO("juan@example.com", contraseniaPlana);
                                        String tokenJWT = "token.jwt.generado";

                                        when(usuarioRepo.findByEmailAndEstadoUsuarioNot(anyString(), any())).thenReturn(Optional.of(usuarioLogin));
                                        when(jwtUtils.generarToken(anyString(), anyMap())).thenReturn(tokenJWT);

                                        // Act
                                        TokenDTO resultado = usuarioService.iniciarSesion(iniciarSesionDTO);

                                        // Assert
                                        assertEquals(tokenJWT, resultado.token());
                                        verify(jwtUtils).generarToken(eq("juan@example.com"), anyMap());
                                    }

                                    @Test
                                    void iniciarSesion_LanzaExcepcionSiCuentaInactiva() {
                                        // Arrange
                                        usuario.setEstadoUsuario(EstadoUsuario.INACTIVO);
                                        IniciarSesionDTO iniciarSesionDTO = new IniciarSesionDTO("juan@example.com", "Clave123*");

                                        when(usuarioRepo.findByEmailAndEstadoUsuarioNot(anyString(), any())).thenReturn(Optional.of(usuario));

                                        // Act & Assert
                                        CuentaInactivaEliminadaException exception = assertThrows(
                                                CuentaInactivaEliminadaException.class,
                                                () -> usuarioService.iniciarSesion(iniciarSesionDTO)
                                        );

                                        assertEquals("Esta cuenta aún no ha sido activada", exception.getMessage());
                                    }

                                    @Test
                                    void incrementarIntentosFallidos_BloquearCuentaDespues5Intentos() throws RecursoNoEncontradoException {
                                        // Arrange
                                        usuario.setFallosInicioSesion(4);
                                        when(usuarioRepo.findByEmailAndEstadoUsuarioNot(anyString(), any())).thenReturn(Optional.of(usuario));

                                        // Act
                                        usuarioService.incrementarIntentosFallidos("juan@example.com");

                                        // Assert
                                        assertEquals(5, usuario.getFallosInicioSesion());
                                        assertNotNull(usuario.getTiempoBloqueo());
                                        verify(usuarioRepo).save(usuario);
                                    }

                                    @Test
                                    void activarCuenta_CambiaEstadoACuando() throws Exception {
                                        // Arrange
                                        String codigo = "ABC123";
                                        CodigoActivacion codigoActivacion = CodigoActivacion.builder()
                                                .codigo(codigo)
                                                .fechaCreacion(LocalDateTime.now())
                                                .build();

                                        Usuario usuarioInactivo = Usuario.builder()
                                                .id("1")
                                                .email("juan@example.com")
                                                .estadoUsuario(EstadoUsuario.INACTIVO)
                                                .codigoActivacion(codigoActivacion)
                                                .build();

                                        ActivarCuentaDTO activarCuentaDTO = new ActivarCuentaDTO("juan@example.com", codigo);

                                        when(usuarioRepo.findByEmailAndEstadoUsuarioNot(anyString(), any())).thenReturn(Optional.of(usuarioInactivo));

                                        // Act
                                        usuarioService.activarCuenta(activarCuentaDTO);

                                        // Assert
                                        assertEquals(EstadoUsuario.ACTIVO, usuarioInactivo.getEstadoUsuario());
                                        verify(usuarioRepo).save(usuarioInactivo);
                                    }

                                    @Test
                                    void activarCuenta_LanzaExcepcionSiCodigoExpirado() {
                                        // Arrange
                                        CodigoActivacion codigoActivacion = CodigoActivacion.builder()
                                                .codigo("ABC123")
                                                .fechaCreacion(LocalDateTime.now().minusMinutes(20)) // Expirado (más de 15 minutos)
                                                .build();

                                        Usuario usuarioInactivo = Usuario.builder()
                                                .id("1")
                                                .email("juan@example.com")
                                                .estadoUsuario(EstadoUsuario.INACTIVO)
                                                .codigoActivacion(codigoActivacion)
                                                .build();

                                        ActivarCuentaDTO activarCuentaDTO = new ActivarCuentaDTO("juan@example.com", "ABC123");

                                        when(usuarioRepo.findByEmailAndEstadoUsuarioNot(anyString(), any())).thenReturn(Optional.of(usuarioInactivo));

                                        // Act & Assert
                                        CodigoExpiradoException exception = assertThrows(
                                                CodigoExpiradoException.class,
                                                () -> usuarioService.activarCuenta(activarCuentaDTO)
                                        );

                                        assertEquals("El código de activación ya expiró", exception.getMessage());
                                    }
                                }