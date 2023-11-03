package vizicard;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import vizicard.model.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import vizicard.model.detail.EducationLevel;
import vizicard.model.detail.EducationType;
import vizicard.repository.*;
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
  private final CardTypeRepository cardTypeRepository;

  public static void main(String[] args) {
    SpringApplication.run(VizicardServiceApp.class, args);
  }

  @Override
  public void run(String... params) throws Exception {
    if (contactTypeRepository.findAll().size() < ContactEnum.class.getEnumConstants().length) {
      System.out.println("WHERE ARE CONTACT TYPES?");
      System.out.println("adding...");
      fillContactTypes();
    }
    if (educationTypeRepository.findAll().size() < EducationLevel.class.getEnumConstants().length) {
      System.out.println("WHERE ARE EDUCATION TYPES?");
      System.out.println("adding...");
      fillEducationTypes();
    }
    if (cardTypeRepository.findAll().size() < CardTypeEnum.class.getEnumConstants().length) {
      System.out.println("WHERE ARE CARD TYPES?");
      System.out.println("adding...");
      fillCardTypes();
    }
  }

  private void fillCardTypes() {
    for (CardTypeEnum type : CardTypeEnum.class.getEnumConstants()) {
      cardTypeRepository.save(new CardType(type, type.toString().toLowerCase()));
    }
  }

  private void fillEducationTypes() {
    educationTypeRepository.save(new EducationType(EducationLevel.PRIMARY, "начальное"));
    educationTypeRepository.save(new EducationType(EducationLevel.SECONDARY, "среднее"));
    educationTypeRepository.save(new EducationType(EducationLevel.HIGHER, "высшее"));
    educationTypeRepository.save(new EducationType(EducationLevel.VOCATIONAL, "профессиональное"));
  }

  void fillContactTypes() {
    Album album = albumRepository.save(new Album());
    ContactGroup contactGroup = contactGroupRepository.save(new ContactGroup(1, ContactGroupEnum.MUSIC, "group", null));

    for (ContactEnum contactEnum : ContactEnum.class.getEnumConstants())
    {
      save(album, contactEnum, contactGroup);
    }
  }

  void save(Album album, ContactEnum contactEnum, ContactGroup contactGroup) {
    ContactType contactType = new ContactType();
    contactType.setType(contactEnum);
    contactType.setWriting(contactEnum.toString().toLowerCase());
    contactType.setLogo(createLogoFor(contactType.getWriting(), album));
    contactType.setGroup(contactGroup);
    contactTypeRepository.save(contactType);
  }

  private CloudFile createLogoFor(String writing, Album album) {
    String keyName = "img_" + writing + ".svg";
    return cloudFileRepository.save(new CloudFile(keyName, album));
  }

}
