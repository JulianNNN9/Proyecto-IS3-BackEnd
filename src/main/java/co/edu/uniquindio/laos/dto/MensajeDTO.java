package co.edu.uniquindio.laos.dto;

public record MensajeDTO<T>(
        boolean error,
        T respuesta
) {
}

