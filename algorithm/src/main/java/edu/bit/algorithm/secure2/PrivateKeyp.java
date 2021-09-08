package edu.bit.algorithm.secure2;

import java.math.BigInteger;

public class PrivateKeyp {

    private final BigInteger n;

    private final BigInteger a;

    public PrivateKeyp(BigInteger n, BigInteger a){
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
