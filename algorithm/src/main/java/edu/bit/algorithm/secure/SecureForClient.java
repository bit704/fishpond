package edu.bit.algorithm.secure;

import javafx.util.Pair;
import org.apache.commons.lang.StringUtils;
import sun.security.rsa.RSAPublicKeyImpl;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 密码安全类
 *
 * testPasswordStrength 测试密码强度
 *
 * RSA加密算法
 * generateKeyPair 生成RSA密钥对(服务端第一次与客户端建立联系时，服务端保存私钥，客户端保存公钥)
 * encryptRSA(客户端，RSA加密)
 * decryptRSA（服务端，RSA解密）
 *
 */
public class SecureForClient {

    /**
     * 判断密码强度函数
     *
     * @param password 密码明文
     * @return String 代表密码强度的数字#具体信息
     * 0: 不通过，密码由6-16个字符组成，且需包含数字、大写字母、小写字母中的至少两种，不允许出现特殊字符
     * 1：弱
     * 2：中
     * 3：强
     */
    public static String testPasswordStrength(String password) {
        int length = password.length();
        //密码长度不符合要求
        if (length < 6 || length > 16) return "0#密码长度不符合要求,应该由6-16个字符组成";
        int digit = 0;
        int lowerCase = 0;
        int upperCase = 0;
        for (int i = 0; i < length; i++) {
            char single = password.charAt(i);
            if (Character.isDigit(single)) {
                digit++;
            } else if (Character.isLowerCase(single)) {
                lowerCase++;
            } else if (Character.isUpperCase(single)) {
                upperCase++;
            } else {
                //包含特殊字符
                return "0#密码中包含特殊字符";
            }
        }
        //字符类型数量小于要求
        int typeNum = (digit + length - 1) / length + (lowerCase + length - 1) / length + (upperCase + length - 1) / length;
        if (typeNum < 2)
            return "0#密码需包含数字、大写字母、小写字母中的至少两种";

        if (length < 10 && typeNum == 2)
            return  "1#密码强度弱";
        if (length < 13 && typeNum == 2)
            return "2#密码强度中";
        else {
            return "3#密码强度强";
        }
    }

    /**
     * 生成RSA密钥对
     * @return RSA密钥对
     * @throws Exception
     */
    public static KeyPair generateKeyPair() {
        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        generator.initialize(2048, new SecureRandom());
        KeyPair pair = generator.generateKeyPair();

        return pair;
    }

    /**
     * RSA加密
     * @param plainText 明文
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception
     */
    public static String encryptRSA(String plainText, PublicKey publicKey) {

        Cipher encryptCipher = null;
        try {
            encryptCipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] cipherText = new byte[0];
        try {
            cipherText = encryptCipher.doFinal(plainText.getBytes(UTF_8));
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(cipherText);
    }

    /**
     * RSA解密
     * @param cipherText 密文
     * @param privateKey 私钥
     * @return 明文
     * @throws Exception
     */
    public static String decryptRSA(String cipherText, PrivateKey privateKey) {
        byte[] bytes = Base64.getDecoder().decode(cipherText);
        Cipher decriptCipher = null;
        try {
            decriptCipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        try {
            return new String(decriptCipher.doFinal(bytes), UTF_8);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将拿到的publickey传输类转化为RSAPublicKeyImpl
     * @param publicKeyDTO
     * @return
     */
    public static RSAPublicKeyImpl getRSAPublicKeyImpl(PublicKeyDTO publicKeyDTO) {
        RSAPublicKeyImpl rsaPublicKey = null;
        try {
            rsaPublicKey = new RSAPublicKeyImpl(publicKeyDTO.getModulus(),publicKeyDTO.getPublicExponent());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return rsaPublicKey;
    }

}
