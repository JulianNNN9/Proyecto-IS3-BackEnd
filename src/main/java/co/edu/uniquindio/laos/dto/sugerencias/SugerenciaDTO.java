package co.edu.uniquindio.laos.dto.sugerencias;

public record SugerenciaDTO (
     String id,
     String nombre,
     String email,
     String mensaje,
     String fecha, // Como String para formatearlo en el frontend
     boolean revisado
){}
