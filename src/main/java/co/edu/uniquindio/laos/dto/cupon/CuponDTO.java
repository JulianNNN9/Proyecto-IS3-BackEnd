package co.edu.uniquindio.laos.dto.cupon;

import co.edu.uniquindio.laos.model.EstadoCupon;

import java.time.LocalDate;

public record CuponDTO(
        String codigo,
        String nombre,
        Double porcentajeDescuento,
        EstadoCupon estadoCupon,
        LocalDate fechaVencimiento
) {}

