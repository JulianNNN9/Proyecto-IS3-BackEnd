package co.edu.uniquindio.laos;

import co.edu.uniquindio.laos.dto.cuenta.CrearUsuarioDTO;
import co.edu.uniquindio.laos.dto.cuenta.IniciarSesionDTO;
import co.edu.uniquindio.laos.model.Usuario;
import co.edu.uniquindio.laos.model.EstadoUsuario;
import co.edu.uniquindio.laos.repositories.UsuarioRepo;
import co.edu.uniquindio.laos.services.interfaces.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class LaosApplicationTests {

    @Autowired
    UsuarioService usuarioService;
    @Autowired
    UsuarioRepo usuarioRepo;

    @Test
    void contextLoads() {
    }

    @Test
    public void iniciarSesionTest() throws Exception {
        //Crear Usuario para encriptar la contraseña:
        CrearUsuarioDTO registroClienteDTO = new CrearUsuarioDTO(
                "12",
                "Julian Andres Hoyoz",
                "Calle 1 # 2-4",
                "3444444444",
                "mi@gmail.com",
                "mipassword"
        );
        String id = usuarioService.crearUsuario(registroClienteDTO);
        //Obtenemos el usuario para cambiarle el estado a ACTIVO
        Usuario usuario = usuarioService.obtenerUsuario(id);
        usuario.setEstadoUsuario(EstadoUsuario.ACTIVO);
        usuarioRepo.save(usuario);

        IniciarSesionDTO iniciarSesionDTO = new IniciarSesionDTO(
                "otroemail@gmail.com", // Email
                "mipassword" // Contraseña
        );

        assertDoesNotThrow(() -> usuarioService.iniciarSesion(iniciarSesionDTO));
        // Comprobamos que el usuario pueda iniciar sesión correctamente.
    }

}
