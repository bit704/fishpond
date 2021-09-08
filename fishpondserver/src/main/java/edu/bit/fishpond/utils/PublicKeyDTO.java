package edu.bit.fishpond.utils;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigInteger;

public class PublicKeyDTO {

    @JSONField
    BigInteger modulus;

    @JSONField
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
