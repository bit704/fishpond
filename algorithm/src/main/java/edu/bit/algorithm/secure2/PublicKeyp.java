package edu.bit.algorithm.secure2;

import java.math.BigInteger;

public class PublicKeyp {

    private final BigInteger n;

    private final BigInteger b;

    public PublicKeyp(BigInteger n, BigInteger b){
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
