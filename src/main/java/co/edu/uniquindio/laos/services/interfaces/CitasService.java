package co.edu.uniquindio.laos.services.interfaces;

import co.edu.uniquindio.laos.dto.cita.CalendarioCitasDTO;
import co.edu.uniquindio.laos.dto.cita.CrearCitaDTO;
import co.edu.uniquindio.laos.dto.cita.InformacionCitaDTO;
import co.edu.uniquindio.laos.dto.cita.ReprogramarCitaDTO;

import java.util.List;

public interface CitasService {

    String crearCita(CrearCitaDTO crearCitaDTO) throws Exception;

    String cancelarCita(String idCita) throws Exception;

    List<InformacionCitaDTO> obtenerCitasCanceladasYCompletadasPorClienteId(String clienteId);

    String reprogramarCita(ReprogramarCitaDTO reprogramarCitaDTO) throws Exception;

    List<InformacionCitaDTO> obtenerCitasPorClienteId(String clienteId);

    List<InformacionCitaDTO> obtenerCitasPorEstilistaId(String estilistaId);

    InformacionCitaDTO obtenerCitaPorId(String citaId) throws Exception;

    List<InformacionCitaDTO> obtenerCitasPorEstado(String estado);

    List<CalendarioCitasDTO> obtenerCitasConfirmadasYReprogramadas();
}
