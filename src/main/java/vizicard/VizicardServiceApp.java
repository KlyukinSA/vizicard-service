package vizicard;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import vizicard.model.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import vizicard.model.detail.EducationLevel;
import vizicard.repository.AlbumRepository;
import vizicard.repository.CloudFileRepository;
import vizicard.repository.ContactGroupRepository;
import vizicard.repository.ContactTypeRepository;
import vizicard.repository.detail.EducationTypeRepository;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class VizicardServiceApp implements CommandLineRunner {
  @Value("${spring.profiles.active}")
  private String activeProfile;

  private final ContactTypeRepository contactTypeRepository;
  private final ContactGroupRepository contactGroupRepository;

  private final EducationTypeRepository educationTypeRepository;
  private final CloudFileRepository cloudFileRepository;
  private final AlbumRepository albumRepository;

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
    if (educationTypeRepository.findAll().size() < EducationLevel.class.getEnumConstants().length) {
      System.out.println("WHERE ARE EDUCATION TYPES?");
//INSERT INTO `dev`.`education_type` (`id`, `type`, `writing`) VALUES ('1', 'PRIMARY', 'НАЧАЛЬНОЕ');
//INSERT INTO `dev`.`education_type` (`id`, `type`, `writing`) VALUES ('2', 'SECONDARY', 'СРЕДНЕЕ');
//INSERT INTO `dev`.`education_type` (`id`, `type`, `writing`) VALUES ('3', 'HIGHER', 'ВЫСШЕЕ');
//INSERT INTO `dev`.`education_type` (`id`, `type`, `writing`) VALUES ('4', 'VOCATIONAL', 'ПРОФЕССИОНАЛЬНОЕ');
    }
  }

  void fillContactTypes() {
    Album album = albumRepository.save(new Album());
    CloudFile logo = cloudFileRepository.save(new CloudFile("empty", album));
    ContactGroup contactGroup = contactGroupRepository.save(new ContactGroup(1, ContactGroupEnum.MUSIC, "group", null));

    for (ContactEnum contactEnum : ContactEnum.class.getEnumConstants())
    {
      save(logo, contactEnum, contactGroup);
    }
  }

  void save(CloudFile logo, ContactEnum contactEnum, ContactGroup contactGroup) {
    ContactType contactType = new ContactType();
    contactType.setType(contactEnum);
    contactType.setLogo(logo);
    contactType.setWriting("type");
    contactType.setGroup(contactGroup);
    contactTypeRepository.save(contactType);
  }

}
