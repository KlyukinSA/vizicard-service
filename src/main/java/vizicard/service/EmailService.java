package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import vizicard.model.*;

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
    private final ShortnameService shortnameService;

    @Value("${spring.mail.username}")
    private String vizicardEmail;
    @Value("${front-url-base}")
    private String urlBase;

    public void sendSaved(Profile actor, Profile target) {
        String to = getAddressTo(actor);
        String subject = "Сохранение контакта";
        String text = getSaveText(target);
        try {
            sendHtml(to, subject, text);
        } catch (Exception e) {
            System.out.println("tried to send message to " + actor.getId() + " about " + target.getId() + "\nbut\n");
            e.printStackTrace();
        }
    }
    
    private void sendHtml(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(vizicardEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        emailSender.send(message);
    }

    private String getAddressTo(Profile target) {
        Optional<Contact> mail = target.getContacts().stream()
                .filter((val) -> val.getType().getType() == ContactEnum.MAIL)
                .findFirst();
        if (mail.isPresent()) {
            return mail.get().getContact();
        }
        return target.getUsername();
    }

    private String getSaveText(Profile profile) {
        String text = getFileText("save-contact-letter.html");
        return substitute(text, profile);
    }

    private String substitute(String text, Profile profile) { // TODO use org.apache.commons.text.StringSubstitutor
        text = replaceArg(text, "url-base", urlBase);
        text = replaceArg(text, "name", profile.getName());
        text = replaceArg(text, "title", profile.getTitle());
        if (profile.getCompany() == null) {
            text = replaceArg(text, "company", null);
        } else {
            text = replaceArg(text, "company", profile.getCompany().getName());
        }
        text = replaceArg(text, "email", getAddressTo(profile));
        text = replaceArg(text, "phone", getPhone(profile));
        text = replaceArg(text, "description", profile.getDescription());
        text = replaceArg(text, "shortname", shortnameService.getMainShortname(profile));
        return text;
    }

    private String replaceArg(String text, String arg, String val) {
        if (val == null) {
            val = "Не указано";
        }
        return text.replaceAll("\\{" + arg + "}", val);
    }

    private String getFileText(String fileName) {
        try {
            InputStream is = new ClassPathResource(fileName).getInputStream();
            return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getPhone(Profile profile) {
        Optional<Contact> phone = profile.getContacts().stream()
                .filter((val) -> val.getType().getType() == ContactEnum.PHONE)
                .findFirst();
        return phone.map(Contact::getContact).orElse(null);
    }

    public void sendLead(Profile target, Profile actor) {
        String to = getAddressTo(target);
        String subject = "Новое знакомство";
        String text = substitute(getFileText("lead-generate-letter.html"), actor);
        try {
            sendHtml(to, subject, text);
        } catch (Exception ignored) {}
    }

}
