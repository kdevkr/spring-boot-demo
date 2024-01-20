package kr.kdev.demo.mail;

import com.google.common.collect.Lists;
import com.sanctionco.jmail.JMail;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.Recipient;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.converter.EmailConverter;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.springsupport.SimpleJavaMailSpringSupport;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Service
@Import(SimpleJavaMailSpringSupport.class)
public class MailService {
    private final Mailer mailer;
    private final JavaMailSenderImpl javaMailSender;
    private final MessageSource messageSource;
    private final SpringResourceTemplateResolver defaultTemplateResolver;

    private SpringTemplateEngine templateEngine;

    @PostConstruct
    public void init() {
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setCacheable(true);
        templateResolver.setCacheTTLMs(Duration.ofMinutes(5).toMillis());

        templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolvers(Set.of(templateResolver, defaultTemplateResolver));
        templateEngine.setTemplateEngineMessageSource(messageSource);
    }

    public String processTemplate(String template, Map<String, Object> variables) {
        return processTemplate(template, variables, Locale.getDefault());
    }

    public String processTemplate(String template, Map<String, Object> variables, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return templateEngine.process(template, new Context(locale, variables));
    }

    public Email prepare(String sender, String mailTitle, String mailContent, Recipient... recipients) {
        List<Recipient> validatedRecipients = Lists.newArrayList(recipients)
                .parallelStream()
                .filter(recipient -> validateAddress("%s<%s>".formatted(recipient.getName(), recipient.getAddress())))
                .toList();

        return EmailBuilder.startingBlank()
                .from(sender)
                .withRecipients(validatedRecipients)
                .withSubject(mailTitle)
                .withHTMLText(mailContent)
                .buildEmail();
    }

    public MimeMessage convertTo(Email email) {
        return EmailConverter.emailToMimeMessage(email);
    }

    @Async
    public void send(Email email) {
        if (mailer.validate(email)) {
            mailer.sendMail(email, true);
        }
    }

    @Async
    public void send(MimeMessage message) {
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            log.error(e.getMessage(), e);
        }
    }

    public boolean validateAddress(String address) {
        if (JMail.isInvalid(address)) {
            try {
                new InternetAddress(address).validate();
            } catch (AddressException e) {
                return false;
            }
        }
        return true;
    }
}
