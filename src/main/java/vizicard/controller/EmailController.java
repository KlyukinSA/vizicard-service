package vizicard.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vizicard.service.EmailService;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping("{to}")
    public void send(@PathVariable("to") String to) {
        emailService.send(to + "@mail.ru");
    }

}
