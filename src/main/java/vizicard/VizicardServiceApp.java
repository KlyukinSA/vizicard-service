package vizicard;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import vizicard.model.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import vizicard.repository.CloudFileRepository;
import vizicard.repository.ContactTypeRepository;

@SpringBootApplication
@RequiredArgsConstructor
public class VizicardServiceApp implements CommandLineRunner {
  @Value("${spring.profiles.active}")
  private String activeProfile;

  private final ContactTypeRepository contactTypeRepository;
  private final CloudFileRepository cloudFileRepository;

  public static void main(String[] args) {
    SpringApplication.run(VizicardServiceApp.class, args);
  }

  @Override
  public void run(String... params) throws Exception {
    if (contactTypeRepository.findAll().size() < ContactEnum.class.getEnumConstants().length) {
      System.out.println("WHERE ARE CONTACT TYPES?");
      if (activeProfile.equals("dev")) {
        System.out.println("adding...");
        fillContactTypes();
      }
    }
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
    contactType.setType(contactEnum);
    contactType.setLogo(cloudFile);
    contactTypeRepository.save(contactType);
  }

}
