package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vizicard.exception.CustomException;
import vizicard.model.Card;
import vizicard.model.CardAttribute;
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

    private final RelationValidator relationValidator;

    private final ModelMapper modelMapper;

    public Contact create(Card card, Contact contact) {
        stopInvalidContactUrl(contact);

        contact.setCardOwner(card);
        contact.setIndividualId(getNextIndividualId(card));
        contactRepository.save(contact);
        contact.setOrder(contact.getId());
        return contactRepository.save(contact);
    }

    private Integer getNextIndividualId(Card card) {
        return contactRepository.findAllByCardOwner(card).stream()
                .mapToInt(CardAttribute::getIndividualId)
                .max().orElse(0) + 1;
    }

    private void stopInvalidContactUrl(Contact contact) {
        if (contact.getType().getType() != ContactEnum.PHONE && contact.getType().getType() != ContactEnum.MAIL &&
                contact.getValue().contains(".") && !contact.getValue().startsWith("http")) {
            throw new CustomException("url should start with http", HttpStatus.BAD_REQUEST);
        }
    }

    public Contact update(Card card, Contact map, Integer id) {
        Contact contact = contactRepository.findByCardOwnerAndIndividualId(card, id);
        modelMapper.map(map, contact);
        return contactRepository.save(contact);
    }

    public void delete(Card card, Integer id) {
        Contact contact = contactRepository.findByCardOwnerAndIndividualId(card, id);
        contact.setStatus(false);
        contactRepository.save(contact);
    }

    public List<Contact> reorder(Card card, List<Integer> ids, List<Integer> orders) {
        Set<Integer> currents = ids.stream().map(id -> contactRepository.findByCardOwnerAndIndividualId(card, id).getOrder()).collect(Collectors.toSet());
        Set<Integer> news = new HashSet<>(orders);
        if (!Objects.equals(currents, news)) {
            throw new CustomException("set of orders must be equal to set of orders of contacts by ids", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        for (int i = 0; i < ids.size(); i++) {
            Contact contact = contactRepository.findByCardOwnerAndIndividualId(card, ids.get(i));
            relationValidator.stopNotOwnerOf(contact.getCardOwner());
            Integer order = orders.get(i);
            if (!Objects.equals(contact.getOrder(), order)) {
                Contact conflict = contactRepository.findByCardOwnerAndOrder(card, order);
                conflict.setOrder(0);
                contactRepository.save(conflict);

                Integer temp = contact.getOrder();
                contact.setOrder(order);
                contactRepository.save(contact);

                conflict.setOrder(temp);
                contactRepository.save(conflict);
            }
        }
        return contactRepository.findAllByCardOwner(card);
    }

    public List<Contact> getAllOfCard(Card card) {
        return contactRepository.findAllByCardOwner(card);
    }

    public Contact findById(Card card, Integer id) {
        return contactRepository.findByCardOwnerAndIndividualId(card, id);
    }

}
