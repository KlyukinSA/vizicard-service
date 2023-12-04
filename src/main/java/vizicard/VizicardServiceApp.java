package vizicard;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import vizicard.model.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import vizicard.model.detail.EducationType;
import vizicard.repository.*;
import vizicard.repository.detail.EducationTypeRepository;

import java.util.ArrayList;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class VizicardServiceApp implements CommandLineRunner {
//  @Value("${spring.profiles.active}")
//  private String activeProfile;

  private final ContactTypeRepository contactTypeRepository;
  private final ContactGroupRepository contactGroupRepository;

  private final EducationTypeRepository educationTypeRepository;
  private final CloudFileRepository cloudFileRepository;
  private final AlbumRepository albumRepository;
  private final CardTypeRepository cardTypeRepository;
  private final ExtensionRepository extensionRepository;
  private final TabTypeRepository tabTypeRepository;

  public static void main(String[] args) {
    SpringApplication.run(VizicardServiceApp.class, args);
  }

  @Override
  public void run(String... params) {
    if (extensionRepository.findAll().size() < 20) {
      System.out.println("WHERE are EXTENSIONS?");
      System.out.println("adding...");
      fillExtentions();
    }
    if (contactTypeRepository.findAll().size() < ContactEnum.class.getEnumConstants().length) {
      System.out.println("WHERE ARE CONTACT TYPES?");
      System.out.println("adding...");
      fillContactTypes();
    }
    if (educationTypeRepository.findAll().size() < 8) {
      System.out.println("WHERE ARE EDUCATION TYPES?");
      System.out.println("adding...");
      fillEducationTypes();
    }
    if (cardTypeRepository.findAll().size() < CardTypeEnum.class.getEnumConstants().length) {
      System.out.println("WHERE ARE CARD TYPES?");
      System.out.println("adding...");
      fillCardTypes();
    }
    if (tabTypeRepository.findAll().size() < TabTypeEnum.values().length) {
      System.out.println("WHERE ARE TAB TYPES?");
      System.out.println("adding...");
      fillTabTypes();
    }
  }

  private void fillTabTypes() {
    tabTypeRepository.save(new TabType(TabTypeEnum.CONTACTS, "Контакты"));
    tabTypeRepository.save(new TabType(TabTypeEnum.RESUME, "Резюме"));
    tabTypeRepository.save(new TabType(TabTypeEnum.MEDIAS, "Медиа"));
    tabTypeRepository.save(new TabType(TabTypeEnum.FILES, "Файлы"));
  }

  private void fillExtentions() {
    String src = "EPS - E4C62D, JPG - D03132, PNG - A166AA, IND - C13E7A, FLA - D03034, MP3 - 176AA9, MOV - 156BA9, HTML - 8DC27A, PHP - E1B221, CSS - DC7F1C, GIF - D82F2F, DOC - 104F7A, DOCX - 104F7A, PDF - D03034, PPT - DC7F1C, XLS - 0D683E, XLSX - 0D683E, CSV - 0D683E, ZIP - E1B31F, OTHER - 536471";
    String[] pairs = src.split(",");
    for (String pair : pairs) {
      String[] els = pair.split("-");
      extensionRepository.save(new Extension(els[0].trim(), els[1].trim()));
    }
  }

  private void fillCardTypes() {
    for (CardTypeEnum type : CardTypeEnum.class.getEnumConstants()) {
      cardTypeRepository.save(new CardType(type, type.toString().toLowerCase()));
    }
  }

  private void fillEducationTypes() {
    educationTypeRepository.save(new EducationType("Среднее"));
    educationTypeRepository.save(new EducationType("Среднее специальное"));
    educationTypeRepository.save(new EducationType("Неоконченное высшее"));
    educationTypeRepository.save(new EducationType("Высшее"));
    educationTypeRepository.save(new EducationType("Бакалавр"));
    educationTypeRepository.save(new EducationType("Магистр"));
    educationTypeRepository.save(new EducationType("Кандидат наук"));
    educationTypeRepository.save(new EducationType("Доктор наук"));
  }

  void fillContactTypes() {
    Album album = albumRepository.save(new Album());
    ContactGroup contactGroup = contactGroupRepository.save(new ContactGroup(1, ContactGroupEnum.MUSIC, "music", null));

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
    contactType.setUrlBase(contactEnum.name() + "UrlBase/");
    contactType.setGroups(new ArrayList<>());
    contactType.getGroups().add(contactGroup);
    contactTypeRepository.save(contactType);
  }

  private CloudFile createLogoFor(String writing, Album album) {
    String usedLogoExtension = "svg";
    String keyName = "img_" + writing + "." + usedLogoExtension;
    return cloudFileRepository.save(new CloudFile(keyName, album, CloudFileType.MEDIA,
            extensionRepository.findByName(usedLogoExtension.toUpperCase()), 0));
  }

}
