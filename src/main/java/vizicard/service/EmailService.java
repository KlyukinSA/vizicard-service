package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    private static final String vizicardEmail = "info@vizicard.ru";

    public void sendTest(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(vizicardEmail);
        message.setTo(to);
        message.setSubject("meet");
        message.setText("person");
        emailSender.send(message);
    }

    public void sendRelation(String to, String fileName, byte[] vCardBytes) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject("Новый контакт в ViziCard");
        helper.setFrom(vizicardEmail);
        helper.setTo(to);
        helper.setText("Вы сохранили контакт. Файл " + fileName + " в приложении к письму.", false);
        helper.addAttachment(fileName, new ByteArrayDataSource(vCardBytes, "text/vcard"));
        emailSender.send(message);
    }
}
