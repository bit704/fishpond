package edu.bit.algorithm.secure;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigInteger;

public class PublicKeyDTO {

    BigInteger modulus;

    BigInteger PublicExponent;

    public PublicKeyDTO(BigInteger modulus, BigInteger publicExponent) {
        this.modulus = modulus;
        PublicExponent = publicExponent;
    }

    public BigInteger getModulus() {
        return modulus;
    }

    public BigInteger getPublicExponent() {
        return PublicExponent;
    }
}
