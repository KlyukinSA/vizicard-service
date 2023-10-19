package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import vizicard.dto.contact.ContactInListRequest;
import vizicard.dto.profile.ProfileUpdateDTO;
import vizicard.exception.CustomException;
import vizicard.model.*;
import vizicard.repository.*;
import vizicard.utils.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

  private final CardRepository cardRepository;
  private final RelationRepository relationRepository;
  private final ContactRepository contactRepository;
  private final ContactTypeRepository contactTypeRepository;

  private final ProfileProvider profileProvider;
  private final ModelMapper modelMapper;
  private final Relator relator;

  private final S3Service s3Service; // TODO use CloudFileRepository

  private final CardService cardService;
  private final AuthService authService;

//  public CardResponse searchByShortname(String shortname) {
//    Shortname shortname1 = shortnameRepository.findByShortname(shortname);
//    Card card = shortname1.getCard();
//    if (card == null) {
//      card = shortname1.getOwner().getMainCard();
//    }
//    return search(card, shortname1);
//  }
//
//  public CardResponse searchById(Integer id) {
//    Card card = profileProvider.getTarget(id);
//    return search(card, null);
//  }
//
//  private CardResponse search(Card card, Shortname shortname) {
//    if (card.getType() == ProfileType.CUSTOM_USER || card.getType() == ProfileType.CUSTOM_COMPANY || card.getType() == ProfileType.GROUP) {
//      relationValidator.stopNotOwnerOf(card);
//    }
//    actionService.addVisitAction(card, shortname);
//    return cardMapper.mapToResponse(card);
//  }

//  public CardResponse whoami() {
//    return cardMapper.mapToResponse(profileProvider.getUserFromAuth());
//  }

//  public Card update(Integer id, ProfileUpdateDTO dto) {
//    Card target = profileProvider.getTarget(id);
//    relationValidator.stopNotOwnerOf(target);
//    return updateProfile(target, dto);
//  }

//  public Card createProfile(ProfileCreateDTO dto, Account owner, String username, String password, RelationType relationType) {
//    Card card = new Card();
//    card.setType(dto.getType());
//    card.setName(dto.getName());
////    card.setUsername(username);
//    card = cardRepository.save(card);
//
//    if (owner != null) {
//      relationRepository.save(new Relation(owner, card, relationType));
//    }
//
//    cardService.create(card);
//
//    ProfileUpdateDTO dto1 = modelMapper.map(dto, ProfileUpdateDTO.class);
////    dto1.setPassword(password);
//    return updateProfile(card, dto1);
//  }

//  public Card createMyProfile(ProfileCreateDTO dto) {
//    Set<ProfileType> relationOrCompanyGroupProfileTypes = new HashSet<>(Arrays.asList(
//            ProfileType.CUSTOM_USER, ProfileType.CUSTOM_COMPANY,
//            ProfileType.COMPANY, ProfileType.GROUP));
//    if (!relationOrCompanyGroupProfileTypes.contains(dto.getType())) {
//      throw new CustomException("cant create with this type", HttpStatus.UNPROCESSABLE_ENTITY);
//    }
//    Card card = new Card();
//    card.setName(dto.getName());
//    card.setType(dto.getType());
//    card.setAccount(profileProvider.getUserFromAuth());
//    cardRepository.save(card);
//    return updateProfile(card, modelMapper.map(dto, ProfileUpdateDTO.class));
//  }

  public Card updateProfile(Card card, ProfileUpdateDTO dto) {
    if (dto.getName() != null) { // TODO set modelMapper how to map contacts and cloudFiles
      card.setName(dto.getName());
    }
    if (dto.getTitle() != null) {
      card.setTitle(dto.getTitle());
    }
    if (dto.getDescription() != null) {
      card.setDescription(dto.getDescription());
    }
    if (dto.getCity() != null) {
      card.setCity(dto.getCity());
    }

    if (dto.getAvatarId() != null) {
      if (dto.getAvatarId().equals(0)) {
        card.setAvatar(null);
      } else {
        card.setAvatar(s3Service.getById(dto.getAvatarId()));
      }
    }

    if (dto.getCompanyId() != null) {
      if (dto.getCompanyId().equals(0)) {
        card.setCompany(null);
      } else {
        Card company = profileProvider.getTarget(dto.getCompanyId());
        card.setCompany(company);

        Relation relation = relationRepository.findByOwnerAndCard(card.getAccount(), company);
        RelationType relationType;
        if (relation != null) {
          relationType = relation.getType();
        } else {
          relationType = RelationType.USUAL;
        }
        relator.relate(card.getAccount(), company, relationType);
      }
    }

    if (dto.getContacts() != null) {
      updateContacts(card, dto.getContacts());
    }

    if (dto.getPassword() != null) {
      authService.changePassword(card, dto.getPassword());
    }

    return cardRepository.save(card);
  }

  private void updateContacts(Card card, List<ContactInListRequest> contacts) {
    Set<ContactEnum> types = contacts.stream()
            .map(ContactInListRequest::getType)
            .collect(Collectors.toSet());
    if (types.size() != contacts.size()) {
      throw new CustomException("Cant update profile when types in list of contacts are not unique", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    for (Contact contact : card.getContacts()) {
      contact.setStatus(false);
    }

//    int order = profile.getContacts().stream().mapToInt(Contact::getOrder).max().orElse(0);
    int order = 0;
    for (ContactInListRequest dto : contacts) {
      order++;
      ContactType contactType = contactTypeRepository.findByType(dto.getType());
      Contact contact = contactRepository.findByOwnerAndOrder(card, order);
      if (contact == null) {
        contact = new Contact();
        contact.setOwner(card);
        contact.setOrder(order);
      } else {
        contact.setStatus(true);
      }
      contact.setType(contactType);
      contact.setContact(dto.getContact());
      contactRepository.save(contact);
//      card.getContacts().add(contact);
    }
  }

//  public void deleteProfile(Integer id) {
//    Card target = profileProvider.getTarget(id);
//    relationValidator.stopNotOwnerOf(target);
//    target.setStatus(false);
//    cardRepository.save(target);
//  }

  public Card mergeCustomProfiles(Integer mainId, Integer secondaryId) {
    if (Objects.equals(mainId, secondaryId)) {
      throw new CustomException("Can merge only different profiles", HttpStatus.FORBIDDEN);
    }
    Card main = profileProvider.getTarget(mainId);
    Card secondary = profileProvider.getTarget(secondaryId);
    if (!checkCanMerge(main, secondary)) {
      throw new CustomException("cant merge profiles with this types", HttpStatus.FORBIDDEN);
    }

    applySecondaryContacts(main, secondary);
    setCustomType(main);
    cardRepository.save(main);

    secondary.setStatus(false);
    cardRepository.save(secondary);
    return main;
  }

  private void applySecondaryContacts(Card main, Card secondary) {
    List<Contact> mainContacts = main.getContacts();
    Set<ContactEnum> mainContactTypes = mainContacts.stream()
            .map((val) -> val.getType().getType())
            .collect(Collectors.toSet());
    int order = mainContacts.size();
    for (Contact contact : secondary.getContacts()) {
      if (!mainContactTypes.contains(contact.getType().getType())) {
        order++;
        contact.setOrder(order);
        contact.setOwner(main);
        mainContacts.add(contact);
      }
    }
    main.setContacts(mainContacts);
  }

  private void setCustomType(Card main) {
    if (main.getType() == ProfileType.CUSTOM_USER || main.getType() == ProfileType.LEAD_USER) {
      main.setType(ProfileType.CUSTOM_USER);
    } else {
      main.setType(ProfileType.CUSTOM_COMPANY);
    }
  }

  private boolean checkCanMerge(Card main, Card secondary) {
    if (main.getType() == ProfileType.CUSTOM_USER || main.getType() == ProfileType.LEAD_USER) {
      return secondary.getType() == ProfileType.CUSTOM_USER || secondary.getType() == ProfileType.LEAD_USER;
    } else if (main.getType() == ProfileType.CUSTOM_COMPANY || main.getType() == ProfileType.LEAD_COMPANY) {
      return secondary.getType() == ProfileType.CUSTOM_COMPANY || secondary.getType() == ProfileType.LEAD_COMPANY;
    } else {
      return false;
    }
  }

//  public List<Card> getSecondaryPrimaryAccounts() {
//    return getProfileWithHisSecondaryAccounts(primaryService.getPrimaryOrSelf(profileProvider.getUserFromAuth()));
//  }

//  private List<Card> getProfileWithHisSecondaryAccounts(Card owner) {
//    List<Card> res = relationRepository.findAllByTypeAndOwner(
//            RelationType.SECONDARY, owner).stream()
//            .map(Relation::getCard)
//            .filter(Card::isStatus)
//            .collect(Collectors.toList());
//    res.add(owner);
//    return res;
//  }

//  public Card createSecondaryProfile(ProfileCreateDTO dto) {
//    Card owner = primaryService.getPrimaryOrSelf(profileProvider.getUserFromAuth());
//    return createProfile(dto, owner, null, null, RelationType.SECONDARY);
//  }

}
