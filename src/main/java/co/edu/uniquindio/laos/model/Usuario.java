package co.edu.uniquindio.laos.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document("usuarios")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String cedula;
    private String nombreCompleto;
    private String direccion;
    private String telefono;
    private String email;
    private String contrasenia;
    private Rol rol;
    private EstadoUsuario estadoUsuario;
    private CodigoActivacion codigoActivacion;
    private CodigoRecuperacion codigoRecuperacion;
    private int fallosInicioSesion;
    private LocalDateTime tiempoBloqueo;
}

