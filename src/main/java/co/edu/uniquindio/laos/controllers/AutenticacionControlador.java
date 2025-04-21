package co.edu.uniquindio.laos.controllers;

import co.edu.uniquindio.laos.dto.MensajeDTO;
import co.edu.uniquindio.laos.dto.TokenDTO;
import co.edu.uniquindio.laos.services.interfaces.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para gestionar la autenticación de usuarios.
 * Proporciona endpoints para la gestión de sesiones y tokens
 * como renovación de tokens JWT expirados.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AutenticacionControlador {

    /**
     * Servicio para gestión de usuarios y autenticación
     */
    private final UsuarioService usuarioService;

    /**
     * Renueva un token JWT expirado sin necesidad de reautenticar al usuario.
     * Este endpoint permite mantener la sesión activa cuando el token original
     * está a punto de expirar o ha expirado recientemente.
     *
     * @param token Token JWT actual o expirado
     * @return Nuevo token JWT válido con tiempo de expiración actualizado
     */
    @GetMapping("/refresh")
    public ResponseEntity<MensajeDTO<TokenDTO>> refresh(@RequestParam("token") String token) {
        return ResponseEntity.ok().body(new MensajeDTO<>(false, usuarioService.refreshToken(token)));
    }
}