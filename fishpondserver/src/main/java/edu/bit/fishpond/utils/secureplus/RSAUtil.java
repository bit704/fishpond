package edu.bit.fishpond.utils.secureplus;

import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;

/**
 * RSA加密解密工具
 */
public class RSAUtil {


    /**
     * 生成密钥对
     *
     * @param bitlength 比特长度
     * @return 密钥对
     */
    public static RSAKeyPair generatorKey(int bitlength) {
        SecureRandom random = new SecureRandom();
        random.setSeed(new Date().getTime());
        BigInteger bigPrimep, bigPrimeq;
        while (!(bigPrimep = BigInteger.probablePrime(bitlength, random)).isProbablePrime(1)) {
            continue;
        }//生成大素数p

        while (!(bigPrimeq = BigInteger.probablePrime(bitlength, random)).isProbablePrime(1)) {
            continue;
        }//生成大素数q

        BigInteger n = bigPrimep.multiply(bigPrimeq);//生成n
        //生成k
        BigInteger k = bigPrimep.subtract(BigInteger.ONE).multiply(bigPrimeq.subtract(BigInteger.ONE));
        //生成一个比k小的b,或者使用65537
        BigInteger b = BigInteger.probablePrime(bitlength - 1, random);
        //根据扩展欧几里得算法生成b
        BigInteger a = cal(b, k);
        //存储入 公钥与私钥中
        RSAPrivateKey privateKey = new RSAPrivateKey(n, a);
        RSAPublicKey publicKey = new RSAPublicKey(n, b);

        //生成秘钥对 返回密钥对
        return new RSAKeyPair(publicKey, privateKey);
    }

    /**
     *  加密函数
     * @param plaintext  明文
     * @param key 公钥
     * @param charset 字符集
     * @return 密文
     */
    public static String encrypt(String plaintext, RSAPublicKey key, String charset) {
        plaintext = "fishpond|" + plaintext;
        byte[] sourceByte = null;
        try {
            sourceByte = plaintext.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        BigInteger temp = new BigInteger(1, sourceByte);
        BigInteger encrypted = temp.modPow(key.getB(), key.getN());
        return JSON.toJSONString(encrypted);
    }

    /**
     * 解密函数
     * @param ciphertext 密文
     * @param key 密钥
     * @param charset 字符集
     * @return 明文
     */
    public static String decrypt(String ciphertext, RSAPrivateKey key, String charset) {
        BigInteger cryptedBig = JSON.parseObject(ciphertext,BigInteger.class);
        byte[] cryptedData = cryptedBig.modPow(key.getA(), key.getN()).toByteArray();
        cryptedData = Arrays.copyOfRange(cryptedData, 0, cryptedData.length);//去除符号位的字节
        String result = "";
        try {
            result = new String(cryptedData, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(result.substring(0,9).equals("fishpond|"))  return result.substring(9);
        return "加密错误或密文被篡改";
    }


    //存储临时的位置变量x，y 用于递归
    private static BigInteger x;
    private static BigInteger y;

    //欧几里得扩展算法
    private static BigInteger ex_gcd(BigInteger a, BigInteger b) {
        if (b.intValue() == 0) {
            x = new BigInteger("1");
            y = new BigInteger("0");
            return a;
        }
        BigInteger ans = ex_gcd(b, a.mod(b));
        BigInteger temp = x;
        x = y;
        y = temp.subtract(a.divide(b).multiply(y));
        return ans;

    }

    //求反模
    private static BigInteger cal(BigInteger a, BigInteger k) {
        BigInteger gcd = ex_gcd(a, k);
        if (BigInteger.ONE.mod(gcd).intValue() != 0) {
            return new BigInteger("-1");
        }
        //由于我们只求乘法逆元 所以这里使用BigInteger.One,实际中如果需要更灵活可以多传递一个参数,表示模的值来代替这里
        x = x.multiply(BigInteger.ONE.divide(gcd));
        k = k.abs();
        BigInteger ans = x.mod(k);
        if (ans.compareTo(BigInteger.ZERO) < 0) ans = ans.add(k);
        return ans;

    }

}


