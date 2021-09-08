package edu.bit.algorithm;

import edu.bit.algorithm.password.PasswordSecure;
import org.junit.jupiter.api.Test;
import java.security.KeyPair;
import java.security.PrivateKey;
import sun.security.rsa.RSAPrivateCrtKeyImpl;

public class passwordTest {

    @Test
    public void test01() {
       String ciphertext = PasswordSecure.encryptPlaintext("555");
       System.out.println(PasswordSecure.verifyPassword("555",ciphertext));

        KeyPair keyPair = PasswordSecure.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        System.out.println();

        System.out.println(PasswordSecure.decryptRSA(PasswordSecure.encryptRSA("555",keyPair.getPublic()),keyPair.getPrivate()));
    }
}
