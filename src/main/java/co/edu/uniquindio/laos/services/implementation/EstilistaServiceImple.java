package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.model.Estilista;
import co.edu.uniquindio.laos.repositories.EstilistaRepo;
import co.edu.uniquindio.laos.services.interfaces.EstilistaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EstilistaServiceImple implements EstilistaService {

    private final EstilistaRepo estilistaRepo;

    @Override
    public List<Estilista> obtenerTodosLosEstilistas() {
        return estilistaRepo.findAll();
    }
}
