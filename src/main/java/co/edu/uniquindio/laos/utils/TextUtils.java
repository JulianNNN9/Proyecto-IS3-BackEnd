package co.edu.uniquindio.laos.utils;

import java.text.Normalizer;

public class TextUtils {

    public static String normalizarTexto(String input) {
        if (input == null) {
            return null;
        }
        // Normaliza el texto eliminando acentos (tildes) y caracteres especiales
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("[^\\p{ASCII}]", "");  // Elimina caracteres no ASCII (tildes, diacríticos)
    }
}