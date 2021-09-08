package edu.bit.algorithm.secure2;

public class RsaKeyPair {

    private final PrivateKeyp privateKey;

    private final PublicKeyp publicKey;

    public RsaKeyPair(PublicKeyp publicKey, PrivateKeyp privateKey){
        this.privateKey=privateKey;
        this.publicKey=publicKey;
    }

    public PrivateKeyp getPrivateKey() {
        return privateKey;
    }

    public PublicKeyp getPublicKey() {
        return publicKey;
    }
}
