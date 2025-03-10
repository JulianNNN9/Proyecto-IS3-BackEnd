package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.dto.cuenta.CrearUsuarioDTO;
import co.edu.uniquindio.laos.model.EstadoUsuario;
import co.edu.uniquindio.laos.model.Rol;
import co.edu.uniquindio.laos.model.Usuario;
import co.edu.uniquindio.laos.repositories.UsuarioRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UsuarioServiceImpleTest {

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Test
    void refreshToken() {
    }

    @Test
    void crearUsuario() {

        Usuario usuario = Usuario.builder()
                .cedula("7843526198")
                .nombreCompleto("admin1")
                .email("admin1@gmail.com")
                .contrasenia(encriptarPassword("admin"))
                .rol(Rol.ADMIN)
                .estadoUsuario(EstadoUsuario.ACTIVO)
                .build();

        usuarioRepo.save(usuario);
    }

    private String encriptarPassword(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode( password );
    }

    @Test
    void editarUsuario() {
    }

    @Test
    void eliminarUsuario() {
    }

    @Test
    void obtenerInformacionUsuario() {
    }

    @Test
    void enviarCodigoRecuperacionCuenta() {
    }

    @Test
    void enviarCodigoActivacionCuenta() {
    }

    @Test
    void recuperarContrasenia() {
    }

    @Test
    void cambiarContrasenia() {
    }

    @Test
    void obtenerUsuario() {
    }

    @Test
    void obtenerUsuarioPorEmail() {
    }

    @Test
    void incrementarIntentosFallidos() {
    }

    @Test
    void iniciarSesion() {
    }

    @Test
    void activarCuenta() {
    }
}