package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.dto.sugerencias.CrearSugerenciaDTO;
import co.edu.uniquindio.laos.dto.sugerencias.SugerenciaDTO;
import co.edu.uniquindio.laos.model.Sugerencia;
import co.edu.uniquindio.laos.repositories.SugerenciaRepository;
import co.edu.uniquindio.laos.services.interfaces.SugerenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.format.DateTimeFormatter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SugerenciaServiceImple implements SugerenciaService {

    private final SugerenciaRepository sugerenciaRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String crearSugerencia(CrearSugerenciaDTO dto) {

        Sugerencia nuevaSugerencia = new Sugerencia();

        nuevaSugerencia.setNombre(dto.nombre());
        nuevaSugerencia.setEmail(dto.email());
        nuevaSugerencia.setMensaje(dto.mensaje());
        nuevaSugerencia.setFecha(LocalDate.now().format(formatter)); // Fecha actual en String
        nuevaSugerencia.setRevisado(false);

        Sugerencia guardada = sugerenciaRepository.save(nuevaSugerencia);

        return guardada.getId();
    }

    @Override
    public List<SugerenciaDTO> obtenerSugerencias() {
        List<Sugerencia> sugerencias = sugerenciaRepository.findAll();

        return sugerencias.stream().map(sugerencia ->
                new SugerenciaDTO(
                        sugerencia.getId(),
                        sugerencia.getNombre(),
                        sugerencia.getEmail(),
                        sugerencia.getMensaje(),
                        sugerencia.getFecha(),
                        sugerencia.isRevisado()
                )
        ).collect(Collectors.toList());
    }

    @Override
    public List<SugerenciaDTO> obtenerSugerenciasPorFecha(String fecha) {
        List<Sugerencia> sugerencias = sugerenciaRepository.findByFecha(fecha);

        return sugerencias.stream().map(sugerencia ->
                new SugerenciaDTO(
                        sugerencia.getId(),
                        sugerencia.getNombre(),
                        sugerencia.getEmail(),
                        sugerencia.getMensaje(),
                        sugerencia.getFecha(),
                        sugerencia.isRevisado()
                )
        ).collect(Collectors.toList());
    }

    @Override
    public void marcarComoRevisado(String id) {
        sugerenciaRepository.findById(id).map(sugerencia -> {
            sugerencia.setRevisado(true);
            sugerenciaRepository.save(sugerencia);
            return true;
        });
    }
}
