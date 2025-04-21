package co.edu.uniquindio.laos.dto.cita;

import co.edu.uniquindio.laos.model.EstadoCita;

import java.time.LocalDateTime;

public record InformacionCitaDTO(

        String citaId,
        String usuarioId,
        String estilistaId,
        String servicioId,
        LocalDateTime fechaHora,
        EstadoCita estado

) {
}
