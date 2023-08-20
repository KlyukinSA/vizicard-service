package vizicard.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vizicard.dto.ContactRequest;
import vizicard.model.Contact;
import vizicard.model.ContactType;
import vizicard.model.Profile;
import vizicard.repository.ContactRepository;
import vizicard.repository.ContactTypeRepository;

@Component
@RequiredArgsConstructor
public class ContactUpdater {

    private final ContactRepository contactRepository;
    private final ContactTypeRepository contactTypeRepository;

    public void updateContact(Profile owner, ContactRequest dto) {
        ContactType contactType = contactTypeRepository.findByContactEnum(dto.getType());
        Contact contact = contactRepository.findByOwnerAndContactType(owner, contactType);
        if (contact != null) {
            contact.setContact(dto.getContact());
        } else {
            contact = new Contact();
            contact.setContactType(contactType);
            contact.setOwner(owner);
            contact.setContact(dto.getContact());
        }
        contactRepository.save(contact);
    }
}
