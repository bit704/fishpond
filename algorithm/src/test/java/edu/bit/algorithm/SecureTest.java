package edu.bit.algorithm;

import com.alibaba.fastjson.JSON;
import edu.bit.algorithm.secure1.PublicKeyDTO;
import edu.bit.algorithm.secure1.SecureForClient;
import edu.bit.algorithm.secure1.SecureForServer;
import edu.bit.algorithm.secure2.*;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.PrivateKey;
import sun.security.rsa.RSAPublicKeyImpl;

public class SecureTest {

    @Test
    public void testSecure1() {
       String ciphertext = SecureForServer.encryptPlaintext("555");
       System.out.println(SecureForServer.verifyPassword("555",ciphertext));

        KeyPair keyPair = SecureForServer.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKeyDTO publicKeyDTO = SecureForServer.getPublicKeyDTO(keyPair);

        String json = JSON.toJSONString(publicKeyDTO);
        System.out.println(json);

        PublicKeyDTO newpublicKeyDTO = JSON.parseObject(json,PublicKeyDTO.class);

        String newjson = JSON.toJSONString(newpublicKeyDTO);
        System.out.println(newjson);

        RSAPublicKeyImpl publicKey = SecureForClient.getRSAPublicKeyImpl(newpublicKeyDTO);

        System.out.println(SecureForServer.decryptRSA(SecureForServer.encryptRSA("555",publicKey),privateKey));
    }

    @Test
    public void testSecure2() {
        RsaKeyPair rsaKeyPair = RSAGeneratorKey.generatorKey(2048);
        PublicKeyp publicKey = rsaKeyPair.getPublicKey();
        PrivateKeyp privateKey = rsaKeyPair.getPrivateKey();
        String ciphertest = SecureForServerp.encryptRSA("徐尘化123456789",publicKey);
        System.out.println(SecureForClientp.decryptRSA(ciphertest,privateKey));
    }
}
