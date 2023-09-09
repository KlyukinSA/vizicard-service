package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import vizicard.model.Contact;
import vizicard.model.ContactEnum;
import vizicard.model.ContactType;
import vizicard.model.Profile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    private static final String vizicardEmail = "info@vizicard.ru";

    public void sendRelation(Profile target, Profile owner) throws MessagingException {
        String text = getRelationText(owner);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(vizicardEmail);
        helper.setTo(getEmailTo(target));
        helper.setSubject("Новый контакт");
        helper.setText(text, true);
        emailSender.send(message);
    }

    private String getEmailTo(Profile target) {
        Optional<Contact> mail = target.getContacts().stream()
                .filter((val) -> val.getType().getType() == ContactEnum.MAIL)
                .findFirst();
        if (mail.isPresent()) {
            return mail.get().getContact();
        }
        return target.getUsername();
    }

    private String getRelationText(Profile owner) {
        try {
            InputStream is = new ClassPathResource("save-contact-letter.html").getInputStream();
            String raw = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            return raw.replaceAll("\\$1", owner.getName()).replaceAll("\\$2", String.valueOf(owner.getId()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendUsual(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(vizicardEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    public void sendLead(Profile target, Profile author) {
        String to = getEmailTo(target);
        String text = author.getName() + " предложил(а) вам знакомство в ViziCard. Ссылка на страницу: https://app.vizicard.ru/" + author.getId();
        String subject = "Новое знакомство";
        sendUsual(to, subject, text);
    }

    public void sendSaved(Profile author, Profile target) {
        String to = getEmailTo(author);
        String text = "Вы сохранили пользователя по имени " + target.getName() + ". Ссылка на страницу: https://app.vizicard.ru/" + target.getId();
        String subject = "Сохранение контакта";
        sendUsual(to, subject, text);
    }

}
