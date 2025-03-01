package co.edu.uniquindio.laos.services.interfaces;

import co.edu.uniquindio.laos.dto.TokenDTO;
import co.edu.uniquindio.laos.dto.cuenta.*;
import co.edu.uniquindio.laos.exceptions.ContraseniaIncorrectaException;
import co.edu.uniquindio.laos.exceptions.CuentaBloqueadaException;
import co.edu.uniquindio.laos.exceptions.CuentaInactivaEliminadaException;
import co.edu.uniquindio.laos.exceptions.RecursoNoEncontradoException;
import co.edu.uniquindio.laos.model.Usuario;

public interface UsuarioService {

    TokenDTO refreshToken(String expiredToken);

    String crearUsuario(CrearUsuarioDTO crearCuentaDTO) throws Exception;

    void editarUsuario(EditarUsuarioDTO editarCuentaDTO) throws Exception;

    void eliminarUsuario(String id) throws Exception;

    InformacionUsuarioDTO obtenerInformacionUsuario(String id) throws RecursoNoEncontradoException;

    void enviarCodigoRecuperacionCuenta(String correo) throws Exception;

    void enviarCodigoActivacionCuenta(String correo) throws Exception;

    void recuperarContrasenia(RecuperarContraseniaDTO recuperarContraseniaDTO) throws Exception;

    void cambiarContrasenia(CambiarContraseniaDTO cambiarContraseniaDTO) throws Exception;

    Usuario obtenerUsuario(String id) throws Exception;

    Usuario obtenerUsuarioPorEmail(String correo) throws RecursoNoEncontradoException;

    void incrementarIntentosFallidos(String correo) throws RecursoNoEncontradoException;

    TokenDTO iniciarSesion(IniciarSesionDTO iniciarSesionDTO) throws RecursoNoEncontradoException,
            CuentaInactivaEliminadaException, CuentaBloqueadaException, ContraseniaIncorrectaException;

    void activarCuenta(ActivarCuentaDTO activarCuentaDTO) throws Exception;

}
