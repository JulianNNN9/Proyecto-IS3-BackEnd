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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AutenticacionControlador {

    private final UsuarioService usuarioService;

    @GetMapping("/refresh")
    public ResponseEntity<MensajeDTO<TokenDTO>> refresh(@RequestParam("token") String token) {
        return ResponseEntity.ok().body(new MensajeDTO<>(false, usuarioService.refreshToken(token)));
    }
}
