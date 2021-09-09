package edu.bit.algorithm.secureplus;

import java.math.BigInteger;

public class RSAPublicKey {

    private final BigInteger n;

    private final BigInteger b;

    public RSAPublicKey(BigInteger n, BigInteger b){
        this.n=n;
        this.b=b;
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getB() {
        return b;
    }
}
