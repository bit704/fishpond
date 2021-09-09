package edu.bit.algorithm;

import com.alibaba.fastjson.JSON;
import edu.bit.algorithm.secure.PublicKeyDTO;
import edu.bit.algorithm.secure.SecureForClient;
import edu.bit.algorithm.secure.SecureForServer;
import edu.bit.algorithm.secureplus.*;
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
        RSAKeyPair rsaKeyPair = RSAUtil.generatorKey(2048);
        RSAPublicKey publicKey = rsaKeyPair.getPublicKey();
        RSAPrivateKey privateKey = rsaKeyPair.getPrivateKey();
        String ciphertext = SecureForClientp.encryptRSA("xuchenhua123456789string",publicKey);
        System.out.println(SecureForServerp.decryptRSA(ciphertext,privateKey));

        String ciphertext2 = SecureForServerp.encryptPBKDF2("好555");
        System.out.println(SecureForServerp.verifyPBKDF2("好555",ciphertext2));
    }
}
