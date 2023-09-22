package vizicard.service;

import com.amazonaws.services.sagemaker.model.ProductionVariant;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vizicard.exception.CustomException;
import vizicard.model.Contact;
import vizicard.repository.ContactRepository;
import vizicard.utils.ProfileProvider;
import vizicard.utils.RelationValidator;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    private final ProfileProvider profileProvider;
    private final RelationValidator relationValidator;

    private final ModelMapper modelMapper;

//    public void changeContacts(ChangeContactsDTO dto) {
//        Profile user = profileProvider.getUserFromAuth();
//        if (dto.getAdd() != null) {
//            for (ContactRequest dto1 : dto.getAdd()) {
//                Contact contact = null;
//
//                if (dto1.getId() != null) {
//                    Optional<Contact> optionalContact = contactRepository.findById(dto1.getId());
//                    if (fits(optionalContact, user)) {
//                        contact = optionalContact.get();
//                        modelMapper.map(dto1, contact);
//                    }
//                } else {
//                    contact = modelMapper.map(dto1, Contact.class);
//                    contact.setOwner(user);
//                }
//
//                try {
//                    contactRepository.save(contact);
//                } catch (Exception e) {
//                    System.out.println("couldnt save contact " + contact);
//                }
//            }
//        }
//        if (dto.getDelete() != null) {
//            for (Integer id : dto.getDelete()) {
//                Optional<Contact> contact = contactRepository.findById(id);
//                if (fits(contact, user)) {
//                    contact.get().setStatus(false);
//                    contactRepository.save(contact.get());
//                }
//            }
//        }
//    }
//
//    private boolean fits(Optional<Contact> contact, Profile user) {
//        return contact.isPresent() && Objects.equals(contact.get().getOwner().getId(), user.getId());
//    }
//
//    public List<Contact> getMyContacts() {
//        return contactRepository.findAllByOwner(profileProvider.getUserFromAuth());
//    }

    public Contact create(Contact contact) {
        contact.setOwner(profileProvider.getUserFromAuth());
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

    public void reorder(Map<Integer, Integer> permutation) {
        Set<?> set = new HashSet<>(permutation.values());
        if (set.size() != permutation.keySet().size()) {
            throw new CustomException("function is not injective", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        for (Contact contact : profileProvider.getUserFromAuth().getContacts()) {
            Integer order = permutation.get(contact.getId());
            if (order != null) {
                contact.setOrder(order);
            }
            contactRepository.save(contact);
        }
    }

}
