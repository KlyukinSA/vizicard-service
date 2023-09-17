package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.*;
import vizicard.model.Contact;
import vizicard.model.ContactType;
import vizicard.repository.ContactGroupRepository;
import vizicard.repository.ContactTypeRepository;
import vizicard.service.ContactService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactTypeRepository contactTypeRepository;
    private final ContactGroupRepository contactGroupRepository;

    private final ContactService contactService;
    private final ModelMapper modelMapper;

    @GetMapping("types")
    public List<ContactTypeResponse> getAllTypes() {
        return contactTypeRepository.findAll().stream()
                .map((val) -> modelMapper.map(val, ContactTypeResponse.class))
                .collect(Collectors.toList());
    }

    @GetMapping("groups")
    public List<ContactGroupResponse> getAllGroups() {
        return contactGroupRepository.findAll().stream()
                .map((val) -> modelMapper.map(val, ContactGroupResponse.class))
                .collect(Collectors.toList());
    }

//    @PutMapping
//    @PreAuthorize("isAuthenticated()")
//    public void changeContacts(@RequestBody ChangeContactsDTO dto) {
//        contactService.changeContacts(dto);
//    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ContactResponse create(@RequestBody ContactCreateDTO dto) {
        ContactType contactType = contactTypeRepository.findByType(dto.getType());
        Contact contact = modelMapper.map(dto, Contact.class);
        contact.setType(contactType);

        contact = contactService.create(contact);

        ContactResponse contactResponse = modelMapper.map(contactService.create(contact), ContactResponse.class);
        contactResponse.setLogoUrl(contact.getType().getLogo().getUrl());
        return contactResponse;
    }

    @PutMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public ContactResponse update(@RequestBody ContactRequest dto, @PathVariable("id") Integer id) {
        Contact contact = modelMapper.map(dto, Contact.class);

        contact = contactService.update(contact, id);

        ContactResponse contactResponse = modelMapper.map(contactService.create(contact), ContactResponse.class);
        contactResponse.setLogoUrl(contact.getType().getLogo().getUrl());
        return contactResponse;
//        return modelMapper.map(contactService.update(modelMapper.map(dto, Contact.class), id), ContactResponse.class);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public void delete(@PathVariable("id") Integer id) {
        contactService.delete(id);
    }

//    @GetMapping
//    @PreAuthorize("isAuthenticated()")
//    List<ContactResponse> getMyContacts() {
//        return contactService.getMyContacts().stream()
//                .map((val) -> modelMapper.map(val, ContactResponse.class))
//                .collect(Collectors.toList());
//    }

}
