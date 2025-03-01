package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.dto.cupon.CrearCuponDTO;
import co.edu.uniquindio.laos.dto.cupon.CuponDTO;
import co.edu.uniquindio.laos.dto.cupon.EditarCuponDTO;
import co.edu.uniquindio.laos.exceptions.RecursoEncontradoException;
import co.edu.uniquindio.laos.exceptions.RecursoNoEncontradoException;
import co.edu.uniquindio.laos.model.Cupon;
import co.edu.uniquindio.laos.model.EstadoCupon;
import co.edu.uniquindio.laos.repositories.CuponRepo;
import co.edu.uniquindio.laos.services.interfaces.CuponService;
import co.edu.uniquindio.laos.utils.TextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CuponServiceImple implements CuponService {

    private final CuponRepo cuponRepo;

    @Override
    public String crearCupon(CrearCuponDTO crearCuponDTO) throws Exception {

        Optional<Cupon> cuponExistente = cuponRepo.findByCodigoAndEstadoNot(crearCuponDTO.codigo(), EstadoCupon.ELIMINADO);

        if (cuponExistente.isPresent()) {
            throw new RecursoEncontradoException("Ya existe un cupón con el código ingresado");
        }
        Cupon cupon = Cupon.builder()
                .codigo(TextUtils.normalizarTexto(crearCuponDTO.codigo()))
                .nombre(TextUtils.normalizarTexto(crearCuponDTO.nombre()))
                .porcentajeDescuento(crearCuponDTO.porcentajeDescuento())
                .estadoCupon(crearCuponDTO.estadoCupon())
                .fechaVencimiento(crearCuponDTO.fechaVencimiento())
                .usuario(crearCuponDTO.usuario())
                .build();

        cuponRepo.save(cupon);

        return "Cupon creado exitosamente";
    }

    @Override
    public String editarCupon(EditarCuponDTO editarCuponDTO) throws RecursoNoEncontradoException {

        Cupon cupon = obtenerCuponPorId(editarCuponDTO.id());

        cupon.setCodigo(editarCuponDTO.codigo());
        cupon.setNombre(editarCuponDTO.nombre());
        cupon.setPorcentajeDescuento(editarCuponDTO.porcentajeDescuento());
        cupon.setEstadoCupon(editarCuponDTO.estadoCupon());
        cupon.setFechaVencimiento(editarCuponDTO.fechaVencimiento());

        cuponRepo.save(cupon);

        return cupon.getId();
    }

    @Override
    public String eliminarCupon(String idCupon) throws RecursoNoEncontradoException {

        Cupon cupon = obtenerCuponPorId(idCupon);

        cupon.setEstadoCupon(EstadoCupon.ELIMINADO);

        cuponRepo.save(cupon);

        return "Cupón eliminado con éxito.";
    }

    @Override
    public Cupon obtenerCuponPorCodigo(String codigo) throws RecursoNoEncontradoException {

        Optional<Cupon> cuponExistente = cuponRepo.findByCodigoAndEstadoNot(codigo, EstadoCupon.ELIMINADO);

        if (cuponExistente.isEmpty()) {
            throw new RecursoNoEncontradoException("Cupón no encontrado");
        }

        return cuponExistente.get();
    }

    @Override
    public List<Cupon> obtenerListaCuponPorIdUsuario(String idUsuario) {
        if (idUsuario.length() != 24) {
            if (idUsuario.length() < 24) {
                // Si es más corto, completar con ceros al final
                idUsuario = String.format("%-24s", idUsuario).replace(' ', '0');
            } else {
                // Si es más largo, recortar a 24 caracteres
                idUsuario = idUsuario.substring(0, 24);
            }
        }
        return cuponRepo.findByUsuarioIdAndEstadoNot(idUsuario, EstadoCupon.ELIMINADO);

    }

    @Override
    public Cupon obtenerCuponPorCodigoYIdUsuario(String codigo, String idUsuario) throws RecursoNoEncontradoException {
        if (idUsuario.length() != 24) {
            if (idUsuario.length() < 24) {
                // Si es más corto, completar con ceros al final
                idUsuario = String.format("%-24s", idUsuario).replace(' ', '0');
            } else {
                // Si es más largo, recortar a 24 caracteres
                idUsuario = idUsuario.substring(0, 24);
            }
        }
        Optional<Cupon> cuponExistente = cuponRepo.findByCodigoAndIdUsuarioAndEstadoNot(codigo, idUsuario, EstadoCupon.ELIMINADO);

        if (cuponExistente.isEmpty()) {
            throw new RecursoNoEncontradoException("Cupón no encontrado");
        }

        return cuponExistente.get();
    }

    @Override
    public Cupon obtenerCuponPorId(String id) throws RecursoNoEncontradoException {
        if (id.length() != 24) {
            if (id.length() < 24) {
                // Si es más corto, completar con ceros al final
                id = String.format("%-24s", id).replace(' ', '0');
            } else {
                // Si es más largo, recortar a 24 caracteres
                id = id.substring(0, 24);
            }
        }
        Optional<Cupon> cuponExistente = cuponRepo.findByIdAndEstadoNot(id, EstadoCupon.ELIMINADO);

        if (cuponExistente.isEmpty()) {
            throw new RecursoNoEncontradoException("Cupón no encontrado");
        }

        return cuponExistente.get();
    }

    @Override
    public List<CuponDTO> listarCupones() {
        return cuponRepo.findAll().stream()
                .map(cupon -> new CuponDTO(
                        cupon.getCodigo(),
                        cupon.getNombre(),
                        cupon.getPorcentajeDescuento(),
                        cupon.getEstadoCupon(),
                        cupon.getFechaVencimiento()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public String generarCodigoCupon() {

        String cadena = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        StringBuilder codigo = new StringBuilder();

        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            codigo.append(cadena.charAt(random.nextInt(cadena.length())));
        }

        return codigo.toString();
    }
}
