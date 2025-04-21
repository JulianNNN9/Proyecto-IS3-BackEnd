package co.edu.uniquindio.laos.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger/OpenAPI para la documentación de la API.
 * Esta clase configura la autenticación mediante tokens JWT en la interfaz
 * de Swagger, permitiendo probar endpoints protegidos directamente desde
 * la documentación interactiva.
 */
@Configuration
@SecurityScheme(
        name = "bearerAuth",          // Nombre del esquema de seguridad
        scheme = "bearer",            // Tipo de esquema (Bearer token)
        bearerFormat = "JWT",         // Formato específico del token
        type = SecuritySchemeType.HTTP, // Tipo de seguridad HTTP
        in = SecuritySchemeIn.HEADER  // El token se envía en la cabecera HTTP
)
public class SwaggerConfig {
    // Esta clase solo define configuración a través de anotaciones,
    // por lo que no requiere métodos adicionales.
}