package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.model.Estilista;
import co.edu.uniquindio.laos.model.Servicio;
import co.edu.uniquindio.laos.repositories.ServiciosRepo;
import co.edu.uniquindio.laos.services.interfaces.ServicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ServicioServiceImple implements ServicioService {

    private final ServiciosRepo serviciosRepo;

    @Override
    public List<Servicio> obtenerTodosLosServicios() {
        return serviciosRepo.findAll();
    }
}
