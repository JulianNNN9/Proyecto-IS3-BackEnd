package co.edu.uniquindio.laos.dto.queja;

import co.edu.uniquindio.laos.model.EstadoQueja;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CrearQuejaDTO (

        @NotBlank(message = "El clienteId no puede estar vacío")
        String clienteId,

        @NotBlank(message = "El nombre del cliente no puede estar vacío")
        String nombreCliente,

        @NotBlank(message = "La descripción no puede estar vacía")
        String descripcion,

        @NotNull(message = "La fecha es obligatoria")
        LocalDateTime fecha,

        @NotBlank(message = "El nombre del servicio no puede estar vacío")
        String nombreServicio,

        @NotBlank(message = "El nombre del estilista no puede estar vacío")
        String nombreEstilista

) {}