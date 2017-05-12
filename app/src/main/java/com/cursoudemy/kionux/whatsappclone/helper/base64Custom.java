package com.cursoudemy.kionux.whatsappclone.helper;

import android.util.Base64;

/**
 * Created by kionux on 17/01/17.
 */

public class base64Custom {
    public static String converterBase64(String texto){
        String textConvertido = Base64.encodeToString(texto.getBytes(),Base64.DEFAULT);
        return textConvertido.trim();
    }
    public static String decodificarBase64(String textoCodificado){
        byte[] byteDecodificado = Base64.decode(textoCodificado,Base64.DEFAULT);
        return new String(byteDecodificado);
    }
}
