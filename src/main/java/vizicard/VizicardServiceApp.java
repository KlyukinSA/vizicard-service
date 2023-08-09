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

import vizicard.repository.CloudFileRepository;
import vizicard.repository.ContactTypeRepository;
import vizicard.service.UserService;

@SpringBootApplication
@RequiredArgsConstructor
public class VizicardServiceApp implements CommandLineRunner {

  final UserService userService;
  final ContactTypeRepository contactTypeRepository;
  final CloudFileRepository cloudFileRepository;
  public static void main(String[] args) {
    SpringApplication.run(VizicardServiceApp.class, args);
  }

  @Override
  public void run(String... params) throws Exception {
//    ContactType contactType = new ContactType();
//    contactType.setContactEnum(ContactEnum.MAIL);
//    CloudFile cloudFile = cloudFileRepository.save(new CloudFile("https://2cc1de15-bc1f377d-9e5a-448f-8a1d-f117b93916d2.s3.timeweb.com/test.txtName"));
//    contactType.setLogo(cloudFile);
//    contactTypeRepository.save(contactType);
  }

}
