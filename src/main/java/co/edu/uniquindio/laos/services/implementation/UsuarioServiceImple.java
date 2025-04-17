package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.config.JWTUtils;
import co.edu.uniquindio.laos.dto.EmailDTO;
import co.edu.uniquindio.laos.dto.TokenDTO;
import co.edu.uniquindio.laos.dto.cuenta.*;
import co.edu.uniquindio.laos.exceptions.*;
import co.edu.uniquindio.laos.model.CodigoRecuperacion;
import co.edu.uniquindio.laos.model.CodigoActivacion;
import co.edu.uniquindio.laos.model.EstadoUsuario;
import co.edu.uniquindio.laos.model.Rol;
import co.edu.uniquindio.laos.model.Usuario;
import co.edu.uniquindio.laos.repositories.UsuarioRepo;
import co.edu.uniquindio.laos.services.interfaces.CuponService;
import co.edu.uniquindio.laos.services.interfaces.EmailService;
import co.edu.uniquindio.laos.services.interfaces.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementación del servicio de usuarios que maneja la lógica de negocio
 * para la gestión de cuentas de usuario en el sistema.
 *
 * Esta clase se encarga de la autenticación, registro, recuperaci��n de contraseña,
 * activación de cuentas y gestión de información de los usuarios.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioServiceImple implements UsuarioService {

    /**
     * Utilidades para la generación y validación de tokens JWT
     */
    private final JWTUtils jwtUtils;

    /**
     * Número máximo de intentos de inicio de sesión fallidos permitidos
     */
    private static final int MAX_FAILED_ATTEMPTS = 5;

    /**
     * Duración del bloqueo temporal de cuenta por intentos fallidos
     */
    private static final Duration LOCK_DURATION = Duration.ofMinutes(5);

    /**
     * Repositorio para el acceso y persistencia de usuarios en la base de datos
     */
    private final UsuarioRepo usuarioRepo;

    /**
     * Servicio para gestionar cupones promocionales
     */
    private final CuponService cuponService;

    /**
     * Servicio para el envío de correos electrónicos
     */
    private final EmailService emailService;

    /**
     * Actualiza un token JWT expirado con un nuevo token válido
     *
     * @param expiredToken Token expirado que se va a refrescar
     * @return TokenDTO con el nuevo token JWT válido
     */
    public TokenDTO refreshToken(String expiredToken) {
        return new TokenDTO(jwtUtils.refreshToken(expiredToken));
    }

    /**
     * Registra un nuevo usuario en el sistema
     *
     * Este método valida que no exista otro usuario con la misma cédula o email,
     * encripta la contraseña, crea una cuenta con estado inactivo y envía
     * un código de activación por correo electrónico.
     *
     * @param crearCuentaDTO Datos necesarios para crear la cuenta
     * @return Identificador único del usuario creado
     * @throws Exception Si la cédula o email ya están en uso
     */
    @Override
    public String crearUsuario(CrearUsuarioDTO crearCuentaDTO) throws Exception {

        if (existeCedula(crearCuentaDTO.cedula())){
            throw new RecursoEncontradoException("La cedula ya está en uso");
        }

        if (existeEmail(crearCuentaDTO.email())){
            throw new RecursoEncontradoException("Este email ya está en uso");
        }

        Usuario nuevoUsuario = Usuario.builder()
                .cedula(crearCuentaDTO.cedula())
                .nombreCompleto(crearCuentaDTO.nombreCompleto())
                .direccion(crearCuentaDTO.direccion())
                .telefono(crearCuentaDTO.telefono())
                .email(crearCuentaDTO.email())
                .contrasenia(encriptarPassword(crearCuentaDTO.contrasenia()))
                .rol(Rol.CLIENTE)
                .estadoUsuario(EstadoUsuario.INACTIVO)
                .fechaRegistro(LocalDateTime.now())
                .build();

        Usuario usuarioGuardado = usuarioRepo.save(nuevoUsuario);

        enviarCodigoActivacionCuenta(crearCuentaDTO.email());

        return usuarioGuardado.getId();
    }

    /**
     * Actualiza la información personal de un usuario existente
     *
     * @param editarCuentaDTO Datos actualizados del usuario
     * @throws RecursoNoEncontradoException Si el usuario no existe
     */
    @Override
    public void editarUsuario(EditarUsuarioDTO editarCuentaDTO) throws RecursoNoEncontradoException {

        Usuario usuario = obtenerUsuario(editarCuentaDTO.idUsuario());

        usuario.setNombreCompleto(editarCuentaDTO.nombreCompleto());
        usuario.setDireccion(editarCuentaDTO.direccion());
        usuario.setTelefono(editarCuentaDTO.telefono());

        usuarioRepo.save(usuario);
    }

    /**
     * Realiza la eliminación lógica de un usuario cambiando su estado a ELIMINADO
     *
     * @param id Identificador único del usuario a eliminar
     * @throws RecursoNoEncontradoException Si el usuario no existe
     */
    @Override
    public void eliminarUsuario(String id) throws RecursoNoEncontradoException {

        Usuario usuario = obtenerUsuario(id);

        usuario.setEstadoUsuario(EstadoUsuario.ELIMINADO);

        usuarioRepo.save(usuario);
    }

    /**
     * Recupera la información personal de un usuario específico
     *
     * @param id Identificador único del usuario
     * @return Objeto InformacionUsuarioDTO con los datos personales
     * @throws RecursoNoEncontradoException Si el usuario no existe
     */
    @Override
    public InformacionUsuarioDTO obtenerInformacionUsuario(String id) throws RecursoNoEncontradoException {

        Usuario usuario = obtenerUsuario(id);

        return new InformacionUsuarioDTO(
                usuario.getCedula(),
                usuario.getNombreCompleto(),
                usuario.getDireccion(),
                usuario.getTelefono(),
                usuario.getEmail()
        );
    }

    /**
     * Envía un código de recuperación a un correo electrónico para restablecer la contraseña
     *
     * Este método genera un código aleatorio, lo almacena asociado al usuario y
     * lo envía por correo para iniciar el proceso de recuperación.
     *
     * @param correo Dirección de correo del usuario que solicita recuperar su contraseña
     * @throws Exception Si ocurre algún error en el proceso
     */
    @Override
    public void enviarCodigoRecuperacionCuenta(String correo) throws Exception {

        try{
            Usuario usuario= obtenerUsuarioPorEmail(correo);

            CodigoRecuperacion codigoRecuperacion = CodigoRecuperacion.builder()
                    .codigo(generarCodigoActivacion())
                    .fechaCreacion(LocalDateTime.now())
                    .build();

            usuario.setCodigoRecuperacion(codigoRecuperacion);
            usuarioRepo.save(usuario);

            EmailDTO emailDTO = new EmailDTO(
                    "Recuperacion de Cuenta",
                    "Su Codigo de Recuperacion es: " + codigoRecuperacion.getCodigo(),
                    correo);

            emailService.enviarCorreo(emailDTO);
        }catch (Exception e){
            return;
        }
    }

    /**
     * Envía un código de activación al correo electrónico de un usuario recién registrado
     *
     * Este método genera un código aleatorio, lo almacena asociado al usuario y
     * lo envía por correo para completar el proceso de activación.
     *
     * @param correo Dirección de correo del usuario a activar
     * @throws Exception Si ocurre algún error en el proceso
     */
    @Override
    public void enviarCodigoActivacionCuenta(String correo) throws Exception {

        try{
            Usuario usuarioActivacion = obtenerUsuarioPorEmail(correo);

            CodigoActivacion codigoActivacion= CodigoActivacion
                    .builder()
                    .codigo(generarCodigoActivacion())
                    .fechaCreacion(LocalDateTime.now())
                    .build();

            usuarioActivacion.setCodigoActivacion(codigoActivacion);
            usuarioRepo.save(usuarioActivacion);

            EmailDTO emailDTO = new EmailDTO(
                    "Activacion de Cuenta",
                    "Su Codigo de Activacion es: " + codigoActivacion.getCodigo(),
                    correo);

            emailService.enviarCorreo(emailDTO);
        }catch (Exception e){
            return;
        }
    }

    /**
     * Completa el proceso de recuperación de contraseña cambiando la contraseña del usuario
     *
     * Este método verifica el código de recuperación, valida que las contraseñas coincidan
     * y actualiza la contraseña del usuario con la nueva contraseña encriptada.
     *
     * @param recuperarContraseniaDTO Datos necesarios para el proceso de recuperación
     * @throws RecursoNoEncontradoException Si el usuario no existe
     * @throws ContraseniaNoCoincidenException Si las contraseñas nuevas no coinciden
     * @throws CodigoExpiradoException Si el código de recuperación ha expirado (más de 15 minutos)
     * @throws CodigoInvalidoException Si el código proporcionado no coincide con el enviado
     * @throws RecursoEncontradoException Si hay algún problema con la recuperación
     */
    @Override
    public void recuperarContrasenia(RecuperarContraseniaDTO recuperarContraseniaDTO) throws RecursoNoEncontradoException, ContraseniaNoCoincidenException, CodigoExpiradoException, CodigoInvalidoException, RecursoEncontradoException {

        Usuario usuario = obtenerUsuarioPorEmail(recuperarContraseniaDTO.correoUsuario());
        if (!Objects.equals(recuperarContraseniaDTO.contraseniaNueva(), recuperarContraseniaDTO.confirmarContraseniaNueva())){
            throw new ContraseniaNoCoincidenException("Las contraseñas no coindicen");
        }

        if (usuario.getCodigoRecuperacion().getFechaCreacion().plusMinutes(15).isBefore(LocalDateTime.now())){
            throw new CodigoExpiradoException("El código expiró");
        }

        if (!usuario.getCodigoRecuperacion().getCodigo().equals(recuperarContraseniaDTO.codigoVerificacion())){
            throw new CodigoInvalidoException("El código es incorrecto");
        }

        usuario.setContrasenia(encriptarPassword(recuperarContraseniaDTO.contraseniaNueva()));

        usuarioRepo.save(usuario);
    }

    /**
     * Cambia la contraseña de un usuario verificando primero la contraseña actual
     *
     * @param cambiarContraseniaDTO Datos necesarios para el cambio de contraseña
     * @throws RecursoNoEncontradoException Si el usuario no existe
     * @throws ContraseniaNoCoincidenException Si las contraseñas nuevas no coinciden
     * @throws ContraseniaIncorrectaException Si la contraseña actual es incorrecta
     */
    @Override
    public void cambiarContrasenia(CambiarContraseniaDTO cambiarContraseniaDTO) throws RecursoNoEncontradoException, ContraseniaNoCoincidenException, ContraseniaIncorrectaException {

        Usuario usuario = obtenerUsuario(cambiarContraseniaDTO.idUsuario());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!cambiarContraseniaDTO.contraseniaNueva().equals(cambiarContraseniaDTO.confirmarContraseniaNueva())){
            throw new ContraseniaNoCoincidenException("Las contraseñas no coindicen");
        }

        if(!passwordEncoder.matches(cambiarContraseniaDTO.contraseniaAntigua(), usuario.getContrasenia())) {
            throw new ContraseniaIncorrectaException("La contraseña es incorrecta");
        }

        usuario.setContrasenia(encriptarPassword(cambiarContraseniaDTO.contraseniaNueva()));

        usuarioRepo.save(usuario);
    }

    /**
     * Busca un usuario por su identificador único
     *
     * Este método normaliza el ID a 24 caracteres si es necesario y verifica
     * que el usuario no esté eliminado.
     *
     * @param id Identificador único del usuario
     * @return Objeto Usuario encontrado
     * @throws RecursoNoEncontradoException Si el usuario no existe o está eliminado
     */
    @Override
    public Usuario obtenerUsuario(String id) throws RecursoNoEncontradoException {
        if (id.length() != 24) {
            if (id.length() < 24) {
                // Si es más corto, completar con ceros al final
                id = String.format("%-24s", id).replace(' ', '0');
            } else {
                // Si es más largo, recortar a 24 caracteres
                id = id.substring(0, 24);
            }
        }
        Optional<Usuario> optionalUsuario = usuarioRepo.findByIdAndEstadoUsuarioNot(id, EstadoUsuario.ELIMINADO);

        if(optionalUsuario.isEmpty()){
            throw new RecursoNoEncontradoException("Usuario no encontrado");
        }
        return optionalUsuario.get();
    }

    /**
     * Busca un usuario por su dirección de correo electrónico
     *
     * @param correo Dirección de correo electrónico
     * @return Objeto Usuario encontrado
     * @throws RecursoNoEncontradoException Si no existe un usuario con ese correo o está eliminado
     */
    @Override
    public Usuario obtenerUsuarioPorEmail(String correo) throws RecursoNoEncontradoException {

        Optional<Usuario> optionalUsuario = usuarioRepo.findByEmailAndEstadoUsuarioNot(correo, EstadoUsuario.ELIMINADO);

        if(optionalUsuario.isEmpty()){
            throw new RecursoNoEncontradoException("Email no encontrado");
        }
        return optionalUsuario.get();
    }

    /**
     * Incrementa el contador de intentos fallidos de inicio de sesión de un usuario
     *
     * Si se alcanza el número máximo de intentos permitidos, se bloquea la cuenta
     * temporalmente según la duración configurada.
     *
     * @param correo Dirección de correo del usuario
     * @throws RecursoNoEncontradoException Si el usuario no existe
     */
    @Override
    public void incrementarIntentosFallidos(String correo) throws RecursoNoEncontradoException {
        Usuario usuario = obtenerUsuarioPorEmail(correo);
        usuario.setFallosInicioSesion(usuario.getFallosInicioSesion() + 1);

        if (usuario.getFallosInicioSesion() >= MAX_FAILED_ATTEMPTS) {
            usuario.setTiempoBloqueo(LocalDateTime.now().plus(LOCK_DURATION));
        }

        usuarioRepo.save(usuario);
    }

    /**
     * Autentica a un usuario y genera un token JWT para su sesión
     *
     * Este método valida las credenciales del usuario, verifica que la cuenta esté activa
     * y no bloqueada, y gestiona los intentos fallidos de inicio de sesión.
     *
     * @param iniciarSesionDTO Credenciales de inicio de sesión (email y contraseña)
     * @return TokenDTO con el token JWT para autenticación
     * @throws RecursoNoEncontradoException Si el usuario no existe
     * @throws CuentaInactivaEliminadaException Si la cuenta no está activada o fue eliminada
     * @throws CuentaBloqueadaException Si la cuenta está temporalmente bloqueada por intentos fallidos
     * @throws ContraseniaIncorrectaException Si la contraseña proporcionada es incorrecta
     */
    @Override
    public TokenDTO iniciarSesion(IniciarSesionDTO iniciarSesionDTO) throws RecursoNoEncontradoException,
            CuentaInactivaEliminadaException, CuentaBloqueadaException, ContraseniaIncorrectaException {

        Usuario usuario = obtenerUsuarioPorEmail(iniciarSesionDTO.email());

        if (usuario.getEstadoUsuario() == EstadoUsuario.INACTIVO){
            throw new CuentaInactivaEliminadaException("Esta cuenta aún no ha sido activada");
        }

        if (usuario.getEstadoUsuario() == EstadoUsuario.ELIMINADO){
            throw new CuentaInactivaEliminadaException("Esta cuenta ha sido eliminada");
        }

        //Manejo del bloqueo de cuenta
        if (estaBloqueada(usuario.getEmail())){
            throw new CuentaBloqueadaException("La cuenta se encuentra bloqueada por demasiados intentos, espere 5 minutos");
        }

        if (usuario.getTiempoBloqueo() != null && LocalDateTime.now().isAfter(usuario.getTiempoBloqueo())){
            desbloquearUsuario(usuario.getEmail());
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if(!passwordEncoder.matches(iniciarSesionDTO.contrasenia(), usuario.getContrasenia())) {
            incrementarIntentosFallidos(usuario.getEmail());
            throw new ContraseniaIncorrectaException("La contraseña es incorrecta");
        }

        desbloquearUsuario(usuario.getEmail());

        Map<String, Object> map = construirClaims(usuario);

        return new TokenDTO(jwtUtils.generarToken(usuario.getEmail(), map));
    }

    /**
     * Activa la cuenta de un usuario utilizando el código de activación enviado
     *
     * Este método verifica que el código sea correcto y no haya expirado,
     * y cambia el estado de la cuenta a ACTIVO.
     *
     * @param activarCuentaDTO Datos necesarios para la activación (email y código)
     * @throws Exception Si ocurre algún error durante la activación
     */
    @Override
    public void activarCuenta(ActivarCuentaDTO activarCuentaDTO) throws Exception {

        Optional<Usuario> usuario = usuarioRepo.findByEmailAndEstadoUsuarioNot(activarCuentaDTO.email(), EstadoUsuario.ELIMINADO);
        if(usuario.isEmpty()){
            throw new RecursoNoEncontradoException("Error al Activar la Cuenta");
        }
        //revisar que se haga dentro del tiempo estipulado
        if (usuario.get().getCodigoActivacion().getFechaCreacion().plusMinutes(15).isBefore(LocalDateTime.now())){
            throw new CodigoExpiradoException("El código de activación ya expiró");
        }
        if(!usuario.get().getCodigoActivacion().getCodigo().equals(activarCuentaDTO.codigoActivacion())){
            throw new CodigoInvalidoException("El código de activación es incorrecto: " + "Codigo Usuario: " + usuario.get().getCodigoActivacion().getCodigo() + "Codigo Enviado: " + activarCuentaDTO.codigoActivacion());
        }
        Usuario usuarioActivacion = usuario.get();
        usuarioActivacion.setEstadoUsuario(EstadoUsuario.ACTIVO);

        usuarioRepo.save(usuarioActivacion);
    }

    /**
     * Verifica si ya existe un usuario con la cédula proporcionada
     *
     * @param cedula Número de cédula a verificar
     * @return true si la cédula ya está registrada, false en caso contrario
     */
    private boolean existeCedula(String cedula) {
        return usuarioRepo.findByCedulaAndEstadoUsuarioNot(cedula, EstadoUsuario.ELIMINADO).isPresent();
    }

    /**
     * Verifica si ya existe un usuario con el correo electrónico proporcionado
     *
     * @param email Dirección de correo electrónico a verificar
     * @return true si el email ya está registrado, false en caso contrario
     */
    private boolean existeEmail(String email) {
        return usuarioRepo.findByEmailAndEstadoUsuarioNot(email, EstadoUsuario.ELIMINADO).isPresent();
    }

    /**
     * Genera un código aleatorio de 6 caracteres para activación o recuperación
     *
     * @return Código generado de 6 caracteres alfanuméricos
     */
    private String generarCodigoActivacion(){

        String cadena = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        StringBuilder codigo = new StringBuilder();

        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            codigo.append(cadena.charAt(random.nextInt(cadena.length())));
        }

        return codigo.toString();
    }

    /**
     * Encripta una contraseña en texto plano utilizando BCrypt
     *
     * @param password Contraseña en texto plano a encriptar
     * @return Versión encriptada de la contraseña
     */
    private String encriptarPassword(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * Verifica si una cuenta está temporalmente bloqueada por intentos fallidos
     *
     * @param correo Dirección de correo del usuario a verificar
     * @return true si la cuenta está bloqueada, false en caso contrario
     * @throws RecursoNoEncontradoException Si el usuario no existe
     */
    private boolean estaBloqueada(String correo) throws RecursoNoEncontradoException {
        Usuario usuario = obtenerUsuarioPorEmail(correo);
        return usuario.getTiempoBloqueo() != null && LocalDateTime.now().isBefore(usuario.getTiempoBloqueo());
    }

    /**
     * Desbloquea una cuenta de usuario y reinicia el contador de intentos fallidos
     *
     * @param username Dirección de correo del usuario a desbloquear
     * @throws RecursoNoEncontradoException Si el usuario no existe
     */
    private void desbloquearUsuario(String username) throws RecursoNoEncontradoException {
        Usuario usuario = obtenerUsuarioPorEmail(username);
        usuario.setFallosInicioSesion(0);
        usuario.setTiempoBloqueo(null);
        usuarioRepo.save(usuario);
    }

    /**
     * Construye un mapa con los datos relevantes del usuario para incluir en el token JWT
     *
     * @param cuenta Usuario cuyos datos se incluirán en el token
     * @return Mapa con los claims (rol, nombre y id) del usuario
     */
    private Map<String, Object> construirClaims(Usuario cuenta) {
        return Map.of(
                "rol", cuenta.getRol(),
                "nombre", cuenta.getNombreCompleto(),
                "id", cuenta.getId()
        );
    }
}