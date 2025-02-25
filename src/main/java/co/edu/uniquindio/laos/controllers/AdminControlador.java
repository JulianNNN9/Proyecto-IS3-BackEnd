package co.edu.uniquindio.laos.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/estilista")
@SecurityRequirement(name = "bearerAuth")
public class AdminControlador {

    /*
    private final ImagenesService imagenesService;
    private final EventoService eventoService;
    private final CuponService cuponService;
     */

}
