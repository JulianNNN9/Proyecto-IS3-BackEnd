package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.dto.quejasugerencia.EnviarQuejaSugerenciaDTO;
import co.edu.uniquindio.laos.dto.quejasugerencia.QuejaSugerenciaDTO;
import co.edu.uniquindio.laos.dto.quejasugerencia.QuejasPorClienteDTO;
import co.edu.uniquindio.laos.dto.quejasugerencia.QuejasPorTipoDTO;
import co.edu.uniquindio.laos.model.EstadoPQRS;
import co.edu.uniquindio.laos.model.QuejaSugerencia;
import co.edu.uniquindio.laos.model.TipoPQRS;
import co.edu.uniquindio.laos.model.Usuario;
import co.edu.uniquindio.laos.repositories.QuejaSugerenciaRepo;
import co.edu.uniquindio.laos.services.interfaces.QuejaSugerenciaService;
import co.edu.uniquindio.laos.services.interfaces.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class QuejaSugerenciaServiceImple implements QuejaSugerenciaService {

    private final QuejaSugerenciaRepo quejaSugerenciaRepo;
    private final UsuarioService usuarioService;

    @Override
    public String registrarQuejaSugerencia(EnviarQuejaSugerenciaDTO enviarQuejaSugerenciaDTO) throws Exception {
        QuejaSugerencia quejaSugerencia = null;
        if(enviarQuejaSugerenciaDTO.cliente() != null && !enviarQuejaSugerenciaDTO.cliente().isEmpty()){
            Usuario usuario = usuarioService.obtenerUsuario(enviarQuejaSugerenciaDTO.cliente());

            quejaSugerencia = QuejaSugerencia.builder()
                    .tipoPQRS(TipoPQRS.valueOf(enviarQuejaSugerenciaDTO.tipo()))
                    .usuario( usuario )
                    .descripcion(enviarQuejaSugerenciaDTO.descripcion())
                    .estadoPQRS(EstadoPQRS.PENDIENTE)
                    .fechaEnvio(LocalDateTime.now())
                    .build();
        }else{
            quejaSugerencia = QuejaSugerencia.builder()
                    .tipoPQRS(TipoPQRS.valueOf(enviarQuejaSugerenciaDTO.tipo()))
                    .descripcion(enviarQuejaSugerenciaDTO.descripcion())
                    .estadoPQRS(EstadoPQRS.PENDIENTE)
                    .fechaEnvio(LocalDateTime.now())
                    .build();
        }

        return quejaSugerenciaRepo.save(quejaSugerencia).getId();

    }

    @Override
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

    @Override
    public List<QuejaSugerenciaDTO> obtenerTodas() {
        return quejaSugerenciaRepo.findAll()
                .stream()
                .map(quejaSugerencia -> new QuejaSugerenciaDTO(
                        quejaSugerencia.getId(),
                        quejaSugerencia.getTipoPQRS().toString(),
                        quejaSugerencia.getUsuario().getId(),
                        quejaSugerencia.getDescripcion(),
                        quejaSugerencia.getEstadoPQRS().toString(),
                        quejaSugerencia.getRespuesta(),
                        quejaSugerencia.getFechaEnvio(),
                        quejaSugerencia.getFechaRespuesta()
                ))
                .collect(Collectors.toList());
    }

    // Obtener número de quejas por tipo
    @Override
    public List<QuejasPorTipoDTO> obtenerQuejasPorTipo() {
        return quejaSugerenciaRepo.contarQuejasPorTipo();
    }

    // Obtener cantidad de quejas por cliente
    @Override
    public List<QuejasPorClienteDTO> obtenerQuejasPorCliente() {
        return quejaSugerenciaRepo.contarQuejasPorUsuario();
    }
    // Obtener el número de quejas/sugerencias de un cliente por tipo
    @Override
    public List<QuejasPorTipoDTO> obtenerQuejasPorClienteYTipo(String cliente) {
        return quejaSugerenciaRepo.contarQuejasPorUsuarioYTipo(cliente);
    }
    @Override
    public List<String> obtenerTipoPqrs() {
        return Arrays.stream(TipoPQRS.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
    @Override
    public List<String> obtenerEstadoPqrs() {
        return Arrays.stream(EstadoPQRS.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }



}
