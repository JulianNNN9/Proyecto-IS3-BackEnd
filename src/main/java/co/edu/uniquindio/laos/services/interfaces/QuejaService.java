package co.edu.uniquindio.laos.services.interfaces;

import co.edu.uniquindio.laos.dto.queja.CrearQuejaDTO;
import co.edu.uniquindio.laos.dto.queja.QuejaDTO;
import co.edu.uniquindio.laos.exceptions.RecursoNoEncontradoException;
import co.edu.uniquindio.laos.model.EstadoQueja;
import co.edu.uniquindio.laos.model.Queja;

import java.time.LocalDateTime;
import java.util.List;

public interface QuejaService {

    String crearQueja(CrearQuejaDTO crearQuejaDTO) throws Exception;

    String eliminarQueja(String idQueja) throws RecursoNoEncontradoException;

    Queja obtenerQuejaPorId(String id) throws RecursoNoEncontradoException;

    List<Queja> obtenerListaQuejasPorServicioId(String servicioId);

    List<Queja> obtenerListaQuejasPorClienteId(String clienteId);

    List<Queja> obtenerListaQuejasPorEstado(EstadoQueja estadoQueja);

    List<Queja> obtenerListaQuejasPorFecha(LocalDateTime startDate, LocalDateTime endDate);

    List<Queja> obtenerListaQuejasPorFechaUnica(LocalDateTime fecha);

    List<Queja> listarQuejas();

    void responderQueja(String idQueja, String respuesta) throws RecursoNoEncontradoException;

}