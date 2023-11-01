package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vizicard.exception.CustomException;
import vizicard.model.Card;
import vizicard.model.Contact;
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
        contact.setOwner(profileProvider.getUserFromAuth().getCurrentCard());
        contactRepository.save(contact);
        contact.setOrder(contact.getId());
        return contactRepository.save(contact);
    }

    public Contact update(Contact map, Integer id) {
        Contact contact = contactRepository.findById(id).get();
        relationValidator.stopNotOwnerOf(contact.getOwner());
        modelMapper.map(map, contact);
        return contactRepository.save(contact);
    }

    public void delete(Integer id) {
        Contact contact = contactRepository.findById(id).get();
        relationValidator.stopNotOwnerOf(contact.getOwner());
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
            relationValidator.stopNotOwnerOf(contact.getOwner());
            Integer order = orders.get(i);
            if (!Objects.equals(contact.getOrder(), order)) {
                Contact conflict = contactRepository.findByOwnerAndOrder(owner, order);
                conflict.setOrder(0);
                contactRepository.save(conflict);

                Integer temp = contact.getOrder();
                contact.setOrder(order);
                contactRepository.save(contact);

                conflict.setOrder(temp);
                contactRepository.save(conflict);
            }
        }
        return contactRepository.findAllByOwner(owner);
    }

    public List<Contact> getOfCurrentCard() {
        return contactRepository.findAllByOwner(profileProvider.getUserFromAuth().getCurrentCard());
    }

}
