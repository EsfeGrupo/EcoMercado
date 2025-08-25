package org.esfe.servicios.utilerias;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CifradoUtil {

    private static final String ALGORITHM = "AES";
    // ¡Usar una clave segura y no hardcodeada en producción!
    private static final byte[] KEY = "claveSecreta123456".getBytes(StandardCharsets.UTF_8);

    public static String encriptar(String dato) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(KEY, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] bytesEncriptados = cipher.doFinal(dato.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(bytesEncriptados);
    }
}
