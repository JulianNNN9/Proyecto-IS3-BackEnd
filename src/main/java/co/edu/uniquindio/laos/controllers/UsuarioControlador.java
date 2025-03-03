package co.edu.uniquindio.laos.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UsuarioControlador {

    /*
    private final UsuarioService usuarioService;
    private final CompraService compraService;
     */

}