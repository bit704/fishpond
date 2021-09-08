package edu.bit.algorithm;

import com.alibaba.fastjson.JSON;
import edu.bit.algorithm.secure.PublicKeyDTO;
import edu.bit.algorithm.secure.SecureForClient;
import edu.bit.algorithm.secure.SecureForServer;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import sun.security.rsa.RSAPublicKeyImpl;

public class SecureTest {

    @Test
    public void test01() {
       //String ciphertext = PasswordSecure.encryptPlaintext("555");
       //System.out.println(PasswordSecure.verifyPassword("555",ciphertext));

        KeyPair keyPair = SecureForServer.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKeyDTO publicKeyDTO = SecureForServer.getPublicKeyDTO(keyPair);

        String json = JSON.toJSONString(publicKeyDTO);
        System.out.println(json);

        PublicKeyDTO newpublicKeyDTO = JSON.parseObject(json,PublicKeyDTO.class);

        RSAPublicKeyImpl publicKey = SecureForClient.getPublicKeyDTO(newpublicKeyDTO);


        System.out.println(SecureForServer.decryptRSA(SecureForServer.encryptRSA("555",publicKey),privateKey));
    }
}
