package co.edu.uniquindio.laos.controllers;

import co.edu.uniquindio.laos.dto.MensajeDTO;
import co.edu.uniquindio.laos.dto.sugerencias.CrearSugerenciaDTO;
import co.edu.uniquindio.laos.services.interfaces.SugerenciaService;
import co.edu.uniquindio.laos.services.interfaces.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UsuarioControlador {


    private final UsuarioService usuarioService;
    private final SugerenciaService sugerenciaService;

    @PostMapping("/crear-sugerencia")
    public ResponseEntity<MensajeDTO<String>> crearSugerencia(@RequestBody CrearSugerenciaDTO dto) {
        sugerenciaService.crearSugerencia(dto);
        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Sugerencia creada correctamente"));
    }

}