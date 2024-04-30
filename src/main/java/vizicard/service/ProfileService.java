package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
  private final ContactService contactService;
  private final ContactTypeRepository contactTypeRepository;
  private final ProfileProvider profileProvider;
  private final AuthService authService;
  private final CompanyService companyService;

  public Card updateProfile(Card card, ProfileUpdateDTO dto) {
    if (dto.getName() != null) {
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
    if (dto.getCardName() != null) {
      card.setCardName(dto.getCardName());
    }

    if (dto.getCompanyName() != null) {
      if (dto.getCompanyName().isEmpty()) {
        companyService.unsetFor(card);
      } else {
        Card company = companyService.getCompanyOf(card);
        if (company != null) {
          company.setName(dto.getCompanyName());
          cardRepository.save(company);
        } else {
          company = new Card();
          company.setName(dto.getName());
          companyService.prepareToCreateOrUpdate(company);
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
    Contact phone = new Contact();
    phone.setValue(dto.getPhone());
    phone.setType(contactTypeRepository.findByType(ContactEnum.PHONE));

    Contact email = new Contact();
    email.setValue(dto.getEmail());
    email.setType(contactTypeRepository.findByType(ContactEnum.MAIL));
    contactService.create(card, email);

    String link = dto.getLink();
    int i = link.indexOf('/') + 1;
    String urlBase = link.substring(0, i);
    ContactType type = contactTypeRepository.findByUrlBase(urlBase);
    Contact linkContact = new Contact();
    linkContact.setValue(link.substring(i));
    linkContact.setType(type);
    contactService.create(card, linkContact);
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
