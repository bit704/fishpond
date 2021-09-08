package edu.bit.algorithm.secure2;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;

public class RSAGeneratorKey {

    /**
     * 生成密钥对
     *
     * @param bitlength 比特长度
     * @return 密钥对
     */
    public static RsaKeyPair generatorKey(int bitlength) {
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
        PrivateKeyp privateKey = new PrivateKeyp(n, a);
        PublicKeyp publicKey = new PublicKeyp(n, b);

        //生成秘钥对 返回密钥对
        return new RsaKeyPair(publicKey, privateKey);
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
