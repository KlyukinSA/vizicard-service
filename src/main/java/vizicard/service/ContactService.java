package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vizicard.exception.CustomException;
import vizicard.model.Card;
import vizicard.model.CardAttribute;
import vizicard.model.Contact;
import vizicard.repository.ContactRepository;
import vizicard.utils.RelationValidator;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    private final RelationValidator relationValidator;

    private final ModelMapper modelMapper;

    public Contact create(Card card, Contact contact) {
        completeValueUpdate(contact);
        contact.setCardOwner(card);
        contact.setIndividualId(getNextIndividualId(card));
        contactRepository.save(contact);
        contact.setOrder(contact.getId());
        return contactRepository.save(contact);
    }

    private void completeValueUpdate(Contact contact) {
        String value = contact.getValue();
        String regexp = contact.getType().getRegex();
        boolean matches = Pattern.compile(regexp).matcher(value).find();
        if (matches) {
            return;
        }
        value = contact.getType().getUrlBase() + value;
        matches = Pattern.compile(regexp).matcher(value).find();
        if (!matches) {
            throw new CustomException("contact value does not fit regexp of contact type", HttpStatus.BAD_REQUEST);
        }
        contact.setValue(value);
    }

    private Integer getNextIndividualId(Card card) {
        return contactRepository.findAllByCardOwner(card).stream()
                .mapToInt(CardAttribute::getIndividualId)
                .max().orElse(0) + 1;
    }

    public Contact update(Card card, Contact map, Integer id) {
        Contact contact = contactRepository.findByCardOwnerAndIndividualId(card, id);
        modelMapper.map(map, contact);
        completeValueUpdate(contact);
        return contactRepository.save(contact);
    }

    public void delete(Card card, Integer id) {
        Contact contact = contactRepository.findByCardOwnerAndIndividualId(card, id);
        contact.setStatus(false);
        contactRepository.save(contact);
    }

    public List<Contact> reorder(Card card, List<Integer> oldOrders, List<Integer> orders) {
        Set<Integer> currents = new HashSet<>(oldOrders);
        Set<Integer> news = new HashSet<>(orders);
        if (!Objects.equals(currents, news)) {
            throw new CustomException("set of orders must be equal to set of old orders", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        Set<Integer> used = new HashSet<>();
        for (int i = 0; i < oldOrders.size(); i++) {
            Contact contact = contactRepository.findByCardOwnerAndOrder(card, oldOrders.get(i));
            relationValidator.stopNotOwnerOf(contact.getCardOwner());
            Integer order = orders.get(i);
            if (!Objects.equals(contact.getOrder(), order) && !used.contains(contact.getOrder())) {
                Contact conflict = contactRepository.findByCardOwnerAndOrder(card, order);
                conflict.setOrder(0);
                contactRepository.save(conflict);

                Integer temp = contact.getOrder();
                contact.setOrder(order);
                contactRepository.save(contact);

                conflict.setOrder(temp);
                contactRepository.save(conflict);

                used.add(order);
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
