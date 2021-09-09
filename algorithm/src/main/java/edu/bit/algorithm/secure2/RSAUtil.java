package edu.bit.algorithm.secure2;

import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;

public class RSAUtil {

    public static String encrypt(String source, PublicKeyp key, String charset) {
        source = "fishpond|" + source;
        byte[] sourceByte = null;
        try {
            sourceByte = source.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        BigInteger temp = new BigInteger(1, sourceByte);
        BigInteger encrypted = temp.modPow(key.getB(), key.getN());
        return JSON.toJSONString(encrypted);
    }

    public static String decrypt(String ciphertest, PrivateKeyp key, String charset) {
        BigInteger cryptedBig = JSON.parseObject(ciphertest,BigInteger.class);
        byte[] cryptedData = cryptedBig.modPow(key.getA(), key.getN()).toByteArray();
        cryptedData = Arrays.copyOfRange(cryptedData, 0, cryptedData.length);//去除符号位的字节
        String result = "";
        try {
            result = new String(cryptedData, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(result);
        if(result.substring(0,9).equals("fishpond|"))  return result.substring(9);
        return result;
    }
}
