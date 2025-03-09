package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.dto.quejasugerencia.EnviarQuejaSugerenciaDTO;
import co.edu.uniquindio.laos.dto.quejasugerencia.QuejaSugerenciaDTO;
import co.edu.uniquindio.laos.dto.quejasugerencia.QuejasPorClienteDTO;
import co.edu.uniquindio.laos.dto.quejasugerencia.QuejasPorTipoDTO;
import co.edu.uniquindio.laos.model.EstadoPQRS;
import co.edu.uniquindio.laos.model.QuejaSugerencia;
import co.edu.uniquindio.laos.model.TipoPQRS;
import co.edu.uniquindio.laos.repositories.QuejaSugerenciaRepo;
import co.edu.uniquindio.laos.services.interfaces.QuejaSugerenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class QuejaSugerenciaServiceImple implements QuejaSugerenciaService {

    private final QuejaSugerenciaRepo quejaSugerenciaRepo;

    public String registrarQuejaSugerencia(EnviarQuejaSugerenciaDTO enviarQuejaSugerenciaDTO) {
        QuejaSugerencia quejaSugerencia = QuejaSugerencia.builder()
                .tipoPQRS(TipoPQRS.valueOf(enviarQuejaSugerenciaDTO.tipo()))
                .cliente(enviarQuejaSugerenciaDTO.cliente())
                .descripcion(enviarQuejaSugerenciaDTO.descripcion())
                .estadoPQRS(EstadoPQRS.PENDIENTE)
                .fechaEnvio(LocalDateTime.now())
                .build();

        return quejaSugerenciaRepo.save(quejaSugerencia).getId();

    }

    public String actualizarEstado(String id, String estado, String respuesta) throws Exception {
        Optional<QuejaSugerencia> optionalQueja = quejaSugerenciaRepo.findById(id);
        if (optionalQueja.isPresent()) {
            QuejaSugerencia queja = optionalQueja.get();
            queja.setEstadoPQRS(EstadoPQRS.valueOf(estado));
            queja.setRespuesta(respuesta);
            queja.setFechaRespuesta(LocalDateTime.now());
            return quejaSugerenciaRepo.save(queja).getId();
        } else {
            throw new Exception("Queja o sugerencia no encontrada");
        }
    }

    public List<QuejaSugerenciaDTO> obtenerTodas() {
        return quejaSugerenciaRepo.findAll()
                .stream()
                .map(quejaSugerencia -> new QuejaSugerenciaDTO(
                        quejaSugerencia.getId(),
                        quejaSugerencia.getTipoPQRS().toString(),
                        quejaSugerencia.getCliente(),
                        quejaSugerencia.getDescripcion(),
                        quejaSugerencia.getEstadoPQRS().toString(),
                        quejaSugerencia.getRespuesta(),
                        quejaSugerencia.getFechaEnvio(),
                        quejaSugerencia.getFechaRespuesta()
                ))
                .collect(Collectors.toList());
    }

    // Obtener número de quejas por tipo
    public List<QuejasPorTipoDTO> obtenerQuejasPorTipo() {
        return quejaSugerenciaRepo.contarQuejasPorTipo();
    }

    // Obtener cantidad de quejas por cliente
    public List<QuejasPorClienteDTO> obtenerQuejasPorCliente() {
        return quejaSugerenciaRepo.contarQuejasPorCliente();
    }
    // Obtener el número de quejas/sugerencias de un cliente por tipo
    public List<QuejasPorTipoDTO> obtenerQuejasPorClienteYTipo(String cliente) {
        return quejaSugerenciaRepo.contarQuejasPorClienteYTipo(cliente);
    }



}
