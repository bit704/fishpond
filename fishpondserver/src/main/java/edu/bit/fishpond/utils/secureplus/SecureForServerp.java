package edu.bit.fishpond.utils.secureplus;

import org.apache.commons.lang.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * 安全类
 *
 * PBKDF2加密算法
 * encryptPlaintext 将明文密码加密获得密文（服务端密码落库）
 * verifyPassword  检验明文密码是否与对应密文对应（服务端密码校验）
 *
 * RSA加密算法
 * generateKeyPair 生成RSA密钥对(服务端第一次与客户端建立联系时，服务端保存私钥，客户端保存公钥)
 * encryptRSA(客户端，RSA加密)
 * decryptRSA（服务端，RSA解密）
 *
 */
public class SecureForServerp {


    /**
     * 对明文加密获得密文
     * PBKDF2加密算法
     *
     * @param password 明文
     * @return 密文
     */
    public static String encryptPBKDF2(String password) {
        return PBKDF2Util.encryptPlaintext(password);
    }


    /**
     * 对输入的明文进行验证,是否符合密文
     *
     * @param password 明文
     * @param ciphertext 密文
     */
    public static boolean verifyPBKDF2(String password, String ciphertext) {
        return PBKDF2Util.verifyPlaintext(password,ciphertext);
    }

    /**
     * 生成RSA密钥对
     * @return RSA密钥对
     * @throws Exception
     */
    public static RSAKeyPair generateKeyPair() {
        return RSAUtil.generatorKey(1024);
    }

    /**
     * RSA加密
     * @param plainText 明文
     * @param RSAPublicKey 公钥
     * @return 密文
     * @throws Exception
     */
    public static String encryptRSA(String plainText, RSAPublicKey RSAPublicKey) {
        return RSAUtil.encrypt(plainText, RSAPublicKey,"UTF-8");
    }

    /**
     * RSA解密
     * @param cipherText 密文
     * @param RSAPrivateKey 私钥
     * @return 明文
     * @throws Exception
     */
    public static String decryptRSA(String cipherText, RSAPrivateKey RSAPrivateKey) {
        return RSAUtil.decrypt(cipherText, RSAPrivateKey,"UTF-8");
    }

}
