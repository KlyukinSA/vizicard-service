package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import vizicard.dto.contact.ContactInListRequest;
import vizicard.dto.profile.request.LeadGenDTO;
import vizicard.dto.profile.request.ProfileUpdateDTO;
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
  private final ContactRepository contactRepository;
  private final ContactTypeRepository contactTypeRepository;

  private final ProfileProvider profileProvider;

  private final CloudFileRepository cloudFileRepository;

  private final AuthService authService;
  private final CompanyService companyService;

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
        card.setAvatarId(null);
      } else {
        Optional<CloudFile> cloudFile = cloudFileRepository.findById(dto.getAvatarId());
        if (cloudFile.isPresent() && cloudFile.get().isStatus()) {
          card.setAvatarId(cloudFile.get().getId());
        }
      }
    }
    if (dto.getBackgroundId() != null) {
      if (dto.getBackgroundId().equals(0)) {
        card.setBackgroundId(null);
      } else {
        Optional<CloudFile> cloudFile = cloudFileRepository.findById(dto.getBackgroundId());
        if (cloudFile.isPresent() && cloudFile.get().isStatus()) {
          card.setBackgroundId(cloudFile.get().getId());
        }
      }
    }

    if (dto.getCompanyName() != null) {
      if (dto.getCompanyName().isEmpty()) {
        companyService.unsetFor(card);
      } else {
        Card company = companyService.getCompanyOf(card);
        if (company != null) {
          company.setName(dto.getCompanyName());
          cardRepository.save(company);
        }
      }
    }

    if (dto.getPassword() != null) {
      Account account = card.getAccount();
      if (account != null) {
        authService.changePassword(account, dto.getPassword());
      }
    }

    return cardRepository.save(card);
  }

  public Card updateCardWithLeadGenCardCreationFields(Card card, LeadGenDTO dto) {
    int i = 1;
    Contact phone = new Contact();
    phone.setContact(dto.getPhone());
    phone.setType(contactTypeRepository.findByType(ContactEnum.PHONE));
    phone.setCardOwner(card);
    phone.setOrder(i);
    phone.setIndividualId(i);
    contactRepository.save(phone);

    i++;
    Contact email = new Contact();
    email.setContact(dto.getEmail());
    email.setType(contactTypeRepository.findByType(ContactEnum.MAIL));
    email.setCardOwner(card);
    email.setOrder(i);
    email.setIndividualId(i);
    contactRepository.save(email);

    i++;
    String link = dto.getLink();
    String urlBase = link.substring(0, link.indexOf('/') + 1);
    ContactType type = contactTypeRepository.findByUrlBase(urlBase);
    Contact linkContact = new Contact();
    linkContact.setContact(link);
    linkContact.setType(type);
    linkContact.setCardOwner(card);
    linkContact.setOrder(i);
    linkContact.setIndividualId(i);
    contactRepository.save(linkContact);
    return card;
  }

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
        contact.setCardOwner(main);
        mainContacts.add(contact);
      }
    }
    main.setContacts(mainContacts);
  }

  private void setCustomType(Card card) {
    card.setCustom(true);
  }

  private boolean checkCanMerge(Card main, Card secondary) {
    if (!(main.isCustom() && secondary.isCustom())) {
      return false;
    } else if (main.getType().getType() == CardTypeEnum.PERSON) {
      return secondary.getType().getType() == CardTypeEnum.PERSON;
    } else if (main.getType().getType() == CardTypeEnum.COMPANY) {
      return secondary.getType().getType() == CardTypeEnum.COMPANY;
    } else {
      return false;
    }
  }

}
