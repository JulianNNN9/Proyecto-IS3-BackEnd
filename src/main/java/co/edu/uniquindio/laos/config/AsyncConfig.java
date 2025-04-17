package co.edu.uniquindio.laos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuración para habilitar operaciones asíncronas en la aplicación.
 * Esta clase permite que los métodos anotados con @Async se ejecuten en hilos separados,
 * mejorando el rendimiento general de la aplicación al no bloquear el hilo principal
 * durante operaciones de larga duración.
 */
@Configuration // Define esta clase como una configuración de Spring
@EnableAsync   // Habilita el procesamiento asíncrono en toda la aplicación
public class AsyncConfig implements AsyncConfigurer {
    // La implementación por defecto de AsyncConfigurer utiliza la configuración
    // estándar de Spring para ejecución asíncrona.

    // Si fuera necesario personalizar el executor de tareas asíncronas,
    // se podrían sobrescribir los métodos de AsyncConfigurer aquí.
}