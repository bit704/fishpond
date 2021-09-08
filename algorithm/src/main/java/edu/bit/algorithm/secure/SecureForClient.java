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
     * 对明文密码加密获得密文密码
     * PBKDF2加密算法
     *
     * @param password 密码明文
     * @return 密码密文
     */
    public static String encryptPlaintext(String password) {
        password = StringUtils.rightPad(password,16," ");
        String salt = "";
        try {
            salt = getSalt(44);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        String ciphertext = "";
        try {
            ciphertext = getPBKDF2(password,salt,1000,56);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        ciphertext += salt;
        return ciphertext;
    }


    /**
     * 对输入的密码进行验证,是否符合密文
     *
     * @param password 明文密码
     * @param ciphertext 处理后的密码
     */
    public static boolean verifyPassword(String password, String ciphertext) {
        password = StringUtils.rightPad(password,16," ");
        String result = null;
        // 取到盐值
        String salt = ciphertext.substring(56);
        try {
            result = getPBKDF2(password, salt,1000,56);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        // 把加密后的密文和原密文进行比较，相同则验证成功，否则失败
        return result.equals(ciphertext.substring(0,56));
    }

    /**
     * 生成随机盐
     *
     * @param  salt_size 盐的长度
     * @return 盐
     * @throws NoSuchAlgorithmException
     */
    private static String getSalt(int salt_size) throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] bytes = new byte[salt_size / 2];
        random.nextBytes(bytes);
        //将byte数组转换为16进制的字符串
        String salt = DatatypeConverter.printHexBinary(bytes);
        return salt;
    }

    /**
     * 根据明文和salt生成密文
     *
     * @param password 密码
     * @param salt 盐
     * @param PBKDF2_ITERATIONS 迭代次数
     * @param hash_size 生成密文的长度
     */
    private static String getPBKDF2(String password, String salt, int PBKDF2_ITERATIONS, int hash_size)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        //将16进制字符串形式的salt转换成byte数组
        byte[] bytes = DatatypeConverter.parseHexBinary(salt);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), bytes, PBKDF2_ITERATIONS, hash_size * 4);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = secretKeyFactory.generateSecret(spec).getEncoded();
        //将byte数组转换为16进制的字符串
        return DatatypeConverter.printHexBinary(hash);
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
    public static RSAPublicKeyImpl getPublicKeyDTO(PublicKeyDTO publicKeyDTO) {
        RSAPublicKeyImpl rsaPublicKey = null;
        try {
            rsaPublicKey = new RSAPublicKeyImpl(publicKeyDTO.getModulus(),publicKeyDTO.getPublicExponent());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return rsaPublicKey;
    }

}
