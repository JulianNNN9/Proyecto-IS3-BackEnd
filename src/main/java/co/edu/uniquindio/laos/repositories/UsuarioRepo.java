package co.edu.uniquindio.laos.repositories;

import co.edu.uniquindio.laos.model.EstadoUsuario;
import co.edu.uniquindio.laos.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepo extends MongoRepository<Usuario, String> {

    @Query("{ '_id': ObjectId(?0), 'estadoUsuario': { $ne: ?1 } }")
    Optional<Usuario> findByIdAndEstadoUsuarioNot(String id, EstadoUsuario estadoUsuario);

    @Query("{ 'cedula': ?0, 'estadoUsuario': { $ne: ?1 } }")
    Optional<Usuario> findByCedulaAndEstadoUsuarioNot(String cedula, EstadoUsuario estadoUsuario);

    @Query("{ 'email': ?0, 'estadoUsuario': { $ne: ?1 } }")
    Optional<Usuario> findByEmailAndEstadoUsuarioNot(String email, EstadoUsuario estadoUsuario);

    @Query("{ 'email': ?0, 'contrasenia': ?1, 'estadoUsuario': { $ne: ?2 } }")
    Optional<Usuario> findByEmailAndContraseniaAndEstadoUsuarioNot(String email, String contrasenia, EstadoUsuario estadoUsuario);

    @Query("{ 'codigoActivacion.codigo': ?0, 'estadoUsuario': { $ne: ?1 } }")
    Optional<Usuario> findByCodigoRegistroCodigoAndEstadoUsuarioNot(String codigoActivacion, EstadoUsuario estadoUsuario);
}