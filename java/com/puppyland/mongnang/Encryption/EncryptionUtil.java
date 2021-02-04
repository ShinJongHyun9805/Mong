package com.puppyland.mongnang.Encryption;

import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
public class EncryptionUtil {

    String iv;
    Key keySpec;
    public Key getAESKey(String kk) throws Exception {

        String key = kk;
        iv = key.substring(0, 16);
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes("UTF-8");
        int len = b.length;
        if (len > keyBytes.length) {
            len = keyBytes.length;
        }
        System.arraycopy(b, 0, keyBytes, 0, len);
        keySpec = new SecretKeySpec(keyBytes, "AES");
        return keySpec;
    }

    public String decAES(String enStr , String kk) throws Exception {
        Key keySpec = getAESKey(kk);
        String str = kk;
        byte[] iv = str.getBytes();
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);
        byte[] byteStr = Base64.decode(enStr.getBytes("UTF-8"),0);
        String decStr = new String(c.doFinal(byteStr), "UTF-8");
        return decStr;
    }


}
