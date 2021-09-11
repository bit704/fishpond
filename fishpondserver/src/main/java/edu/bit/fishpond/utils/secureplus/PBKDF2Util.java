package edu.bit.fishpond.utils.secureplus;

import org.apache.commons.lang.StringUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * PBKDF2加密及验证工具
 */
public class PBKDF2Util {

    /**
     * 对明文加密获得密文
     * PBKDF2加密算法
     *
     * @param password 明文
     * @return 密文
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
     * 对输入的明文进行验证,是否符合密文
     *
     * @param password 明文
     * @param ciphertext 密文
     */
    public static boolean verifyPlaintext(String password, String ciphertext) {
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

}
