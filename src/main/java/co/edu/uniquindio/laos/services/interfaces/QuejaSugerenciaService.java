package co.edu.uniquindio.laos.services.interfaces;

import co.edu.uniquindio.laos.dto.quejasugerencia.EnviarQuejaSugerenciaDTO;
import co.edu.uniquindio.laos.dto.quejasugerencia.QuejaSugerenciaDTO;
import co.edu.uniquindio.laos.dto.quejasugerencia.QuejasPorClienteDTO;
import co.edu.uniquindio.laos.dto.quejasugerencia.QuejasPorTipoDTO;

import java.util.List;

public interface QuejaSugerenciaService {

    String registrarQuejaSugerencia(EnviarQuejaSugerenciaDTO quejaSugerencia);

    String actualizarEstado(String id, String estado, String respuesta) throws Exception;

    List<QuejaSugerenciaDTO> obtenerTodas();

    List<QuejasPorTipoDTO> obtenerQuejasPorTipo();

    List<QuejasPorClienteDTO> obtenerQuejasPorCliente();

    List<QuejasPorTipoDTO> obtenerQuejasPorClienteYTipo(String cliente);

}
