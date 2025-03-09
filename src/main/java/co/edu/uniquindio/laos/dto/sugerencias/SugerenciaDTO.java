package co.edu.uniquindio.laos.dto.sugerencias;

public record SugerenciaDTO (
     String id,
     String nombre,
     String email,
     String motivo,
     String mensaje,
     String fecha, // Como String para formatearlo en el frontend
     boolean revisado
){}
