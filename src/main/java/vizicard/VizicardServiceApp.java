package vizicard;

import java.util.ArrayList;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import vizicard.dto.UserSignupDTO;
import vizicard.model.*;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import vizicard.repository.ContactTypeRepository;
import vizicard.service.UserService;

@SpringBootApplication
@RequiredArgsConstructor
public class VizicardServiceApp implements CommandLineRunner {

  final UserService userService;
  final ContactTypeRepository contactTypeRepository;

  public static void main(String[] args) {
    SpringApplication.run(VizicardServiceApp.class, args);
  }

  @Override
  public void run(String... params) throws Exception {
    ContactType contactType = new ContactType();
    contactType.setContactEnum(ContactEnum.MAIL);
    contactType.setLogoId(55);
    contactTypeRepository.save(contactType);
  }

}
