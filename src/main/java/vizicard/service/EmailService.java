package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    public void send(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("myadress@gmail.com");
        message.setTo(to);
        message.setSubject("meet");
        message.setText("person");

        System.out.println(message);
        emailSender.send(message);
        System.out.println(message);
    }
}
