package edu.bit.algorithm.secureplus;

import java.util.StringJoiner;

public class SQLFilterUtil {

    //过滤字符
    private static String[] FilterChars = {" ", "<", ">", "\"", "&", "/", "\\", "\n"};

    /**
     * 分割字符串
     *
     * @param str         要分割的字符串
     * @param spilit_sign 字符串的分割标志
     * @return 分割后得到的字符串数组
     */
    public static String[] stringSpilit(String str, String spilit_sign) {
        return str.split(spilit_sign);
    }

    /**
     * 用特殊的连接字符将字符串数组拼接成字符串
     *
     * @param strings     要连接的字符串数组
     * @param spilit_sign 连接字符
     * @return 连接字符串
     */
    public static String stringConnect(String[] strings, String spilit_sign) {
        StringJoiner stringJoiner = new StringJoiner(spilit_sign);
        for (String string : strings) {
            stringJoiner.add(string);
        }
        return stringJoiner.toString();
    }

    /**
     * 过滤字符串里的的特殊字符
     *
     * @param str 要过滤的字符串
     * @return 过滤后的字符串
     */
    public static String stringFilter(String str) {
        String[] str_arr = stringSpilit(str, "");
        for (int i = 0; i < str_arr.length; i++) {
            for (int j = 0; j < FilterChars.length; j++) {
                if (FilterChars[j].equals(str_arr[i]))
                    str_arr[i] = "";
            }
        }
        return (stringConnect(str_arr, "")).trim();
    }


    /**
     * 字符串字符集转换
     *
     * @param str 要转换的字符串
     * @return 转换过的字符串
     */
    public static String stringTransCharset(String str, String before, String after) {
        String new_str = null;
        try {
            new_str = new String(str.getBytes(before), after);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new_str;
    }
}