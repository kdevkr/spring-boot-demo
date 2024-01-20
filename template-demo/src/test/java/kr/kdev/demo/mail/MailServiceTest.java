package kr.kdev.demo.mail;

import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.Recipient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.FileCopyUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ActiveProfiles("mambo")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("메일 전송 테스트")
class MailServiceTest {

    @Autowired
    MailProperties mailProperties;

    @Autowired
    MailService mailService;

    @DisplayName("Textual template")
    @Test
    void Test_sendEmail_from_TextTemplate_withHtml() {
        Assertions.assertDoesNotThrow(() -> {
            ClassPathResource resource = new ClassPathResource("templates/mail/2fa.html");
            String html = FileCopyUtils.copyToString(new BufferedReader(new InputStreamReader(resource.getInputStream())));
            // String html = """
            //     <h1>Two-Factor Authentication</h1>
            //     <h2>Hi, [[${name}]]</h2>
            //     <p>The two-step authentication code for the login request is as follows.</p>
            //     <p>Please enter it on the authentication screen within the time limit.</p>
            //     <p>Verification Code: <span style="font-size:20px;">[[${code}]]</span></p>
            //     """;
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", "Mambo");
            variables.put("code", 123456);
            variables.put("now", Instant.now());

            String sender = "Mambo <%s>".formatted(mailProperties.getUsername());
            String htmlContent = mailService.processTemplate(html, variables, Locale.forLanguageTag("en"));
            Recipient recipient = new Recipient("Mambo", "kdevkr@gmail.com", Message.RecipientType.TO);
            Email email = mailService.prepare(sender, "[Auth] Requested Two Factor Authentication", htmlContent, recipient);
            MimeMessage mimeMessage = mailService.convertTo(email);

            mailService.send(email); // NOTE: for debugging.
            Assertions.assertDoesNotThrow(() -> mailService.send(mimeMessage));
        });
    }

    @ValueSource(strings = {
            "Mambo<kdevkr@gmail.com>"
            , "Mambo <kdevkr@gmail.com>"
            , "\"Mambo\" <kdevkr@gmail.com>"
            , "Mambo@<kdevkr@gmail.com>"
            , "\uD83D\uDE0A <kdevkr@gmail.com>"
    })
    @ParameterizedTest
    void Test_validateAddress(String address) {
        Assertions.assertTrue(mailService.validateAddress(address));
    }
}