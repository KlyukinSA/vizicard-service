package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    private static final String vizicardEmail = "info@vizicard.ru";

    public void sendRelation(String to, String ownerName, Integer ownerId) throws MessagingException {
        String text = getRelationText(ownerName, String.valueOf(ownerId));

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(vizicardEmail);
        helper.setTo(to);
        helper.setSubject("Новый контакт");
        helper.setText(text, true);
        emailSender.send(message);
    }

    private String getRelationText(String name, String id) {
        try {
            InputStream is = new ClassPathResource("save-contact-letter.html").getInputStream();
            String raw = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            return raw.replaceAll("\\$1", name).replaceAll("\\$2", id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendUsual(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(vizicardEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}
