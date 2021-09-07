package edu.bit.algorithm;

import edu.bit.algorithm.password.passwordSecure;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;

public class passwordTest {

    @Test
    public void test01() {
       String ciphertext = passwordSecure.encryptPlaintext("555");
       System.out.println(passwordSecure.verifyPassword("555",ciphertext));

        KeyPair keyPair = passwordSecure.generateKeyPair();
        System.out.println(passwordSecure.decryptRSA(passwordSecure.encryptRSA("555",keyPair.getPublic()),keyPair.getPrivate()));
    }
}
