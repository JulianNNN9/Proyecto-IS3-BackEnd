package co.edu.uniquindio.laos.services.interfaces;

import co.edu.uniquindio.laos.dto.cupon.CrearCuponDTO;
import co.edu.uniquindio.laos.dto.cupon.CuponDTO;
import co.edu.uniquindio.laos.dto.cupon.EditarCuponDTO;
import co.edu.uniquindio.laos.exceptions.RecursoNoEncontradoException;
import co.edu.uniquindio.laos.model.Cupon;

import java.util.List;

public interface CuponService {

    String crearCupon(CrearCuponDTO crearCuponDTO) throws Exception;

    String editarCupon(EditarCuponDTO editarCuponDTO)throws RecursoNoEncontradoException;

    String eliminarCupon(String idCupon)throws RecursoNoEncontradoException;

    Cupon obtenerCuponPorCodigo(String id) throws RecursoNoEncontradoException;

    List<Cupon> obtenerListaCuponPorIdUsuario(String idUsuario);

    Cupon obtenerCuponPorCodigoYIdUsuario(String codigo, String idUsuario) throws RecursoNoEncontradoException;

    Cupon obtenerCuponPorId(String codigo) throws RecursoNoEncontradoException;

    List<CuponDTO> listarCupones();

    String generarCodigoCupon();

}
