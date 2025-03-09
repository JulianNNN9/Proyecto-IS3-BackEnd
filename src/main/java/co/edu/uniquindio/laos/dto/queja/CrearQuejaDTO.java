package co.edu.uniquindio.laos.dto.queja;

import co.edu.uniquindio.laos.model.EstadoQueja;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CrearQuejaDTO (

        @NotBlank(message = "El clienteId no puede estar vacío")
        String clienteId,

        @NotBlank(message = "La descripción no puede estar vacía")
        String descripcion,

        @NotNull(message = "La fecha es obligatoria")
        LocalDateTime fecha,

        @NotBlank(message = "El estado de la queja no puede estar vacío")
        EstadoQueja estadoQueja,

        @NotBlank(message = "El servicioId no puede estar vacío")
        String servicioId
) {}