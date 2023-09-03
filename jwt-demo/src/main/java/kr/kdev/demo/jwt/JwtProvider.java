package kr.kdev.demo.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class JwtProvider {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtProvider(JwtProperties jwtProperties) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        this.privateKey = getPrivateKey(jwtProperties);
        this.publicKey = getPublicKey(jwtProperties);
    }

    private PublicKey getPublicKey(JwtProperties jwtProperties) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        File file = new ClassPathResource(jwtProperties.getPublicKey(), JwtProvider.class.getClassLoader()).getFile();
        try (FileReader keyReader = new FileReader(file);
             PemReader pemReader = new PemReader(keyReader)) {

            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            return keyFactory.generatePublic(new X509EncodedKeySpec(content));
        }
    }

    private PrivateKey getPrivateKey(JwtProperties jwtProperties) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        File file = new ClassPathResource(jwtProperties.getPrivateKey(), JwtProvider.class.getClassLoader()).getFile();
        try (FileReader keyReader = new FileReader(file);
             PemReader pemReader = new PemReader(keyReader)) {

            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(content));
        }
    }

    public String createJwt(String username, Date expiration, String issuer, Map<String, ?> claims) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        if (expiration == null) {
            expiration = Date.from(Instant.ofEpochMilli(now.toEpochMilli() + Duration.ofHours(1).toMillis()));
        }
        if (claims == null) {
            claims = new HashMap<>();
        }
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setExpiration(expiration)
                .setIssuedAt(issuedAt)
                .setIssuer(issuer)
                .signWith(privateKey, SignatureAlgorithm.RS512)
                .compact();
    }
}
