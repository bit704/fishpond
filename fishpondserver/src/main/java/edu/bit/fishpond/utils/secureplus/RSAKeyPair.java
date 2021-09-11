package edu.bit.fishpond.utils.secureplus;

public class RSAKeyPair {

    private final RSAPrivateKey privateKey;

    private final RSAPublicKey publicKey;

    public RSAKeyPair(RSAPublicKey publicKey, RSAPrivateKey privateKey){
        this.privateKey=privateKey;
        this.publicKey=publicKey;
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }
}
