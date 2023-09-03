package kr.kdev.demo.keypair;


import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

@DisplayName("RSA Key Pair")
class RSAKeyPairTest {

    @DisplayName("Generate key pair")
    @Test
    void TestGenerateRSAKeyPair() {
        Assertions.assertDoesNotThrow(() -> {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(4096, new SecureRandom());
            KeyPair keyPair = generator.generateKeyPair();

            try (PemWriter pubPemWriter = new PemWriter(
                    new OutputStreamWriter(new FileOutputStream("pub.key"), StandardCharsets.UTF_8));
                 PemWriter privPemWriter = new PemWriter(
                         new OutputStreamWriter(new FileOutputStream("priv.key"), StandardCharsets.UTF_8))) {
                pubPemWriter.writeObject(new PemObject("PUBLIC_KEY", keyPair.getPublic().getEncoded()));
                privPemWriter.writeObject(new PemObject("PRIVATE_KEY", keyPair.getPrivate().getEncoded()));
            }
        });
    }
}
