package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.dto.queja.CrearQuejaDTO;
import co.edu.uniquindio.laos.dto.queja.QuejaDTO;
import co.edu.uniquindio.laos.model.*;
import co.edu.uniquindio.laos.repositories.QuejaRepo;
import co.edu.uniquindio.laos.services.interfaces.QuejaService;
import co.edu.uniquindio.laos.exceptions.RecursoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuejaServiceImple implements QuejaService {

    @Autowired
    private QuejaRepo quejaRepo;

    @Override
    public String crearQueja(CrearQuejaDTO crearQuejaDTO) throws Exception {
        Queja queja = Queja.builder()
                .clienteId(crearQuejaDTO.clienteId())
                .nombreCliente(crearQuejaDTO.nombreCliente())
                .descripcion(crearQuejaDTO.descripcion())
                .fecha(crearQuejaDTO.fecha())
                .estadoQueja(EstadoQueja.SIN_RESPONDER)
                .nombreServicio(crearQuejaDTO.nombreServicio())
                .nombreEstilista(crearQuejaDTO.nombreEstilista())
                .respuestaQueja(null)
                .build();
        quejaRepo.save(queja);
        return queja.getId();
    }

    @Override
    public String eliminarQueja(String idQueja) throws RecursoNoEncontradoException {
        Queja queja = quejaRepo.findById(idQueja)
                .orElseThrow(() -> new RecursoNoEncontradoException("Queja no encontrada"));
        if(queja.getEstadoQueja().equals(EstadoQueja.RESPONDIDA)) {
            throw new RecursoNoEncontradoException("No se puede eliminar una queja respondida");
        }
        if(queja.getEstadoQueja().equals(EstadoQueja.ELIMINADA)) {
            throw new RecursoNoEncontradoException("La queja ya ha sido eliminada anteriormente");
        }
        queja.setEstadoQueja(EstadoQueja.ELIMINADA);
        return idQueja;
    }

    @Override
    public Queja obtenerQuejaPorId(String id) throws RecursoNoEncontradoException {
        Optional<Queja> QuejaExistente = quejaRepo.findById(id);

        if (QuejaExistente.isEmpty()) {
            throw new RecursoNoEncontradoException("Cupón no encontrado");
        }

        if (QuejaExistente.get().getEstadoQueja().equals(EstadoQueja.ELIMINADA)){
            throw new RecursoNoEncontradoException("Cupón no encontrado");
        }

        Queja queja = QuejaExistente.get();
        return queja;
    }

    @Override
    public List<Queja> obtenerListaQuejasPorServicioId(String servicioId) {
        return quejaRepo.findByServicioId(servicioId);
    }

    @Override
    public List<Queja> obtenerListaQuejasPorClienteId(String clienteId) {
        return quejaRepo.findByClienteId(clienteId);
    }

    @Override
    public List<Queja> obtenerListaQuejasPorEstado(EstadoQueja estadoQueja) {
        return quejaRepo.findByEstadoQueja(String.valueOf(estadoQueja));
    }

    @Override
    public List<Queja> obtenerListaQuejasPorFecha(LocalDateTime startDate, LocalDateTime endDate) {
        return quejaRepo.findByFechaBetween(startDate, endDate);
    }

    @Override
    public List<Queja> obtenerListaQuejasPorFechaUnica(LocalDateTime fecha) {
        return quejaRepo.findByFecha(fecha);
    }

    @Override
    public List<Queja> listarQuejas() {
        return quejaRepo.findByEstadoQueja("SIN_RESPONDER");
    }

    @Override
    public void responderQueja(String idQueja, String respuesta) throws RecursoNoEncontradoException {
        Queja queja = quejaRepo.findById(idQueja)
                .orElseThrow(() -> new RecursoNoEncontradoException("Queja no encontrada"));
        queja.setRespuestaQueja(new RespuestaQueja(respuesta, LocalDateTime.now()));
        queja.setEstadoQueja(EstadoQueja.RESPONDIDA);
        quejaRepo.save(queja);
    }
}