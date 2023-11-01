package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.contact.*;
import vizicard.mapper.ContactMapper;
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
    private final ContactMapper contactMapper;

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

        return contactMapper.mapToResponse(contact);
    }

    @PutMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public ContactResponse update(@RequestBody ContactRequest dto, @PathVariable("id") Integer id) {
        Contact contact = modelMapper.map(dto, Contact.class);
        contact = contactService.update(contact, id);
        return contactMapper.mapToResponse(contact);
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

    @PutMapping("order")
    @PreAuthorize("isAuthenticated()")
    public List<ContactResponse> reorder(@RequestBody List<ContactReorderDTO> dto) {
        List<Integer> ids = dto.stream().map(ContactReorderDTO::getId).collect(Collectors.toList());
        List<Integer> orders = dto.stream().map(ContactReorderDTO::getOrder).collect(Collectors.toList());
        return contactMapper.mapList(contactService.reorder(ids, orders));
    }

    @GetMapping("my")
    @PreAuthorize("isAuthenticated()")
    public List<ContactResponse> getOfCurrentCard() {
        return contactMapper.mapList(contactService.getOfCurrentCard());
    }

}
