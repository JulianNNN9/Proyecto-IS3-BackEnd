package co.edu.uniquindio.laos.services.interfaces;

import co.edu.uniquindio.laos.dto.sugerencias.CrearSugerenciaDTO;
import co.edu.uniquindio.laos.dto.sugerencias.SugerenciaDTO;

import java.util.List;

public interface SugerenciaService {

    String crearSugerencia(CrearSugerenciaDTO dto);

    List<SugerenciaDTO> obtenerSugerencias();

    List<SugerenciaDTO> obtenerSugerenciasPorFecha(String fecha);

    void marcarComoRevisado(String id);

}
