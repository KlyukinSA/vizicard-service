package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vizicard.exception.CustomException;
import vizicard.model.Card;
import vizicard.model.Contact;
import vizicard.model.ContactEnum;
import vizicard.repository.ContactRepository;
import vizicard.utils.ProfileProvider;
import vizicard.utils.RelationValidator;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    private final ProfileProvider profileProvider;
    private final RelationValidator relationValidator;

    private final ModelMapper modelMapper;

    public Contact create(Contact contact) {
        if (contact.isFull() && contact.getDescription() == null) {
            throw new CustomException("cant create full contact without description", HttpStatus.BAD_REQUEST);
        }
        stopInvalidContactUrl(contact);

        contact.setCardOwner(profileProvider.getUserFromAuth().getCurrentCard());
        contactRepository.save(contact);
        contact.setOrder(contact.getId());
        return contactRepository.save(contact);
    }

    private void stopInvalidContactUrl(Contact contact) {
        if (contact.getType().getType() != ContactEnum.PHONE && contact.getType().getType() != ContactEnum.MAIL &&
                contact.getContact().contains(".") && !contact.getContact().startsWith("http")) {
            throw new CustomException("url should start with http", HttpStatus.BAD_REQUEST);
        }
    }

    public Contact update(Contact map, Integer id) {
        stopInvalidContactUrl(map);
        Contact contact = contactRepository.findById(id).get();
        relationValidator.stopNotOwnerOf(contact.getCardOwner());
        modelMapper.map(map, contact);
        return contactRepository.save(contact);
    }

    public void delete(Integer id) {
        Contact contact = contactRepository.findById(id).get();
        relationValidator.stopNotOwnerOf(contact.getCardOwner());
        contact.setStatus(false);
        contactRepository.save(contact);
    }

    public List<Contact> reorder(List<Integer> ids, List<Integer> orders) {
        Set<Integer> currents = ids.stream().map(id -> contactRepository.findById(id).get().getOrder()).collect(Collectors.toSet());
        Set<Integer> news = new HashSet<>(orders);
        if (!Objects.equals(currents, news)) {
            throw new CustomException("set of orders must be equal to set of orders of contacts by ids", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Card owner = profileProvider.getUserFromAuth().getCurrentCard();
        for (int i = 0; i < ids.size(); i++) {
            Contact contact = contactRepository.findById(ids.get(i)).get();
            relationValidator.stopNotOwnerOf(contact.getCardOwner());
            Integer order = orders.get(i);
            if (!Objects.equals(contact.getOrder(), order)) {
                Contact conflict = contactRepository.findByCardOwnerAndOrder(owner, order);
                conflict.setOrder(0);
                contactRepository.save(conflict);

                Integer temp = contact.getOrder();
                contact.setOrder(order);
                contactRepository.save(contact);

                conflict.setOrder(temp);
                contactRepository.save(conflict);
            }
        }
        return contactRepository.findAllByCardOwner(owner);
    }

    public List<Contact> getOfCurrentCard() {
        return contactRepository.findAllByCardOwner(profileProvider.getUserFromAuth().getCurrentCard());
    }

}
