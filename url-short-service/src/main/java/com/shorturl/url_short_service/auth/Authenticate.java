package com.shorturl.url_short_service.auth;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Configuration
public class Authenticate {

    @Value("${auth.decryptkey}")
    private String decryptKey;

    @Value("${auth.apikey}")
    private String apiKey;

    // Method to decrypt the data
    public Boolean decrypt(String token) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // Convert the decryptKey to a 16-byte key
        SecretKeySpec key = new SecretKeySpec(this.decryptKey.getBytes(), "AES");

        // Create and initialize the cipher
        Cipher cipher = null; // Use the same mode and padding

        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(decryptKey.getBytes())); // Use the same IV

        // Decode the base64 encoded string and decrypt
        byte[] decodedBytes = Base64.getDecoder().decode(token);
        byte[] decryptedBytes = new byte[0];


        decryptedBytes = cipher.doFinal(decodedBytes);

        System.out.println("in the decrypt method");
//        System.out.println(this.apiKey + decryptedBytes);
//        System.out.println(this.apiKey + Arrays.toString(decryptedBytes));
//        System.out.println(this.apiKey + new String(decryptedBytes));
//        System.out.println(this.apiKey == decryptedBytes.toString());

        if (this.apiKey.equals(new String(decryptedBytes))) {
            return true;
        } else {
            return false;
        }
    }
}
