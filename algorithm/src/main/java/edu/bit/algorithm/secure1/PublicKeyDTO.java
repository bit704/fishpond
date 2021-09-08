package edu.bit.algorithm.secure1;

import java.math.BigInteger;

public class PublicKeyDTO {

    private BigInteger modulus;
    private BigInteger PublicExponent;

    public void setModulus(BigInteger modulus) {
        this.modulus = modulus;
    }

    public void setPublicExponent(BigInteger publicExponent) {
        PublicExponent = publicExponent;
    }

    public BigInteger getModulus() {
        return modulus;
    }

    public BigInteger getPublicExponent() {
        return PublicExponent;
    }
}
