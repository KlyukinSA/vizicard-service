package vizicard;

import lombok.RequiredArgsConstructor;
import vizicard.model.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
  }

  void fillContactTypes() {
    CloudFile cloudFile = cloudFileRepository.save(new CloudFile("empty"));

    for (ContactEnum contactEnum : ContactEnum.class.getEnumConstants())
    {
      save(cloudFile, contactEnum);
    }
  }

  void save(CloudFile cloudFile, ContactEnum contactEnum) {
    ContactType contactType = new ContactType();
    contactType.setContactEnum(contactEnum);
    contactType.setLogo(cloudFile);
    contactTypeRepository.save(contactType);
  }

}
