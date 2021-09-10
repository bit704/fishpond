package edu.bit.algorithm.secureplus;

import java.math.BigInteger;

public class RSAPrivateKey {

    private final BigInteger n;

    private final BigInteger a;

    public RSAPrivateKey(BigInteger n, BigInteger a){
        this.n=n;
        this.a=a;
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getA() {
        return a;
    }
}
