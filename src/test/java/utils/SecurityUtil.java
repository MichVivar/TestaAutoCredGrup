package utils;

import java.util.Base64;

public class SecurityUtil {

    // El "Encriptador" (Para que tú generes los códigos)
    public static String encrypt(String texto) {
        if (texto == null) return null;
        return Base64.getEncoder().encodeToString(texto.getBytes());
    }


    public static String decrypt(String textoCifrado) {
        if (textoCifrado == null || textoCifrado.isEmpty()) return textoCifrado;

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(textoCifrado);
            return new String(decodedBytes);
        } catch (IllegalArgumentException e) {
            return textoCifrado;
        }
    }


    // Método auxiliar para saber si el dato ya viene descifrado
    private static boolean usuarioEsTextoPlano(String texto) {
        // Si no tiene el '=' típico de Base64 o es muy corto, 
        // probablemente es texto plano (como "20405")
        return texto == null || !texto.contains("="); 
    }
}