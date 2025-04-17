package co.edu.uniquindio.laos.utils;

import java.text.Normalizer;

/**
 * Clase de utilidad para operaciones de procesamiento de texto.
 *
 * Proporciona métodos para manipular y normalizar texto, facilitando
 * operaciones como la eliminación de caracteres especiales y acentos.
 */
public class TextUtils {

    /**
     * Normaliza una cadena de texto eliminando acentos y caracteres especiales.
     *
     * Este método procesa el texto de entrada para convertirlo a una forma normalizada
     * sin acentos (tildes), diacríticos u otros caracteres especiales no ASCII.
     * Es útil para operaciones de comparación, búsqueda o almacenamiento donde
     * se requiere texto sin caracteres especiales.
     *
     * @param input Texto que se desea normalizar, puede ser null
     * @return El texto normalizado sin acentos ni caracteres especiales,
     *         o null si el parámetro de entrada es null
     */
    public static String normalizarTexto(String input) {
        if (input == null) {
            return null;
        }
        // Normaliza el texto eliminando acentos (tildes) y caracteres especiales
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("[^\\p{ASCII}]", "");  // Elimina caracteres no ASCII (tildes, diacríticos)
    }
}