package edu.bit.algorithm.secureplus;


import javax.crypto.*;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AESUtil {

    public static SecretKey generateKey() throws NoSuchAlgorithmException { // 生成密钥
        KeyGenerator secretGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = new SecureRandom();
        secretGenerator.init(secureRandom);
        SecretKey secretKey = secretGenerator.generateKey();
        return secretKey;
    }

    static Charset charset = Charset.forName("UTF-8");

    public static byte[] encrypt(byte[] content, SecretKey secretKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException { // 加密
        return AES(content, Cipher.ENCRYPT_MODE, secretKey);
    }

    public static byte[] decrypt(byte[] content, SecretKey secretKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException { // 解密
        return AES(content, Cipher.DECRYPT_MODE, secretKey);
    }

    private static byte[] AES(byte[] contentArray, int mode, SecretKey secretKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(mode, secretKey);
        byte[] result = cipher.doFinal(contentArray);
        return result;
    }


}
