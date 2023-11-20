package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.contact.*;
import vizicard.mapper.ContactMapper;
import vizicard.model.Contact;
import vizicard.model.ContactEnum;
import vizicard.model.ContactGroup;
import vizicard.model.ContactType;
import vizicard.repository.CloudFileRepository;
import vizicard.repository.ContactGroupRepository;
import vizicard.repository.ContactTypeRepository;
import vizicard.repository.CustomContactTypeRepository;
import vizicard.service.ContactService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactTypeRepository contactTypeRepository;
    private final CustomContactTypeRepository customContactTypeRepository;
    private final ContactGroupRepository contactGroupRepository;
    private final CloudFileRepository cloudFileRepository;

    private final ContactService contactService;
    private final ModelMapper modelMapper;
    private final ContactMapper contactMapper;

    @GetMapping("types")
    public List<ContactTypeResponse> getAllTypes() {
        return contactTypeRepository.findAll().stream()
                .map(contactMapper::mapToContactTypeResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("types/{type}")
    public ContactTypeResponse getTypeByType(@PathVariable ContactEnum type) {
        return contactMapper.mapToContactTypeResponse(contactTypeRepository.findByType(type));
    }

    @GetMapping("types/search")
    public List<ContactGroupResponse> searchTypeLike(@RequestParam(required = false) String contactType, @RequestParam(required = false) String groupType, @RequestParam(required = false) String theirWriting) {
        List<ContactType> types = customContactTypeRepository.findAllByLikeContactTypeOrGroupTypeOrTheirWriting(contactType, groupType, theirWriting);
        List<ContactGroup> groups = contactGroupRepository.findAll();
        List<ContactGroupResponse> res = groups.stream()
                .map(g -> new ContactGroupResponse(g.getType(), g.getWriting(), getTypeResponsesByGroup(g, types)))
                .collect(Collectors.toList());
        return res;
    }

    private List<ContactTypeResponse> getTypeResponsesByGroup(ContactGroup group, List<ContactType> types) {
        return types.stream()
                .filter(t -> t.getGroup().getId().equals(group.getId()))
                .map(contactMapper::mapToContactTypeResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("groups")
    public List<ContactGroupResponse> getAllGroups() {
        return contactGroupRepository.findAll().stream()
                .map(contactMapper::mapToContactGroupResponse)
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
        Contact contact = getMapFromContactRequest(dto);
        contact.setType(contactType);

        contact = contactService.create(contact);

        return contactMapper.mapToResponse(contact);
    }

    @PutMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public FullContactResponse update(@RequestBody ContactRequest dto, @PathVariable("id") Integer id) {
        Contact contact = getMapFromContactRequest(dto);
        contact = contactService.update(contact, id);
        return contactMapper.mapToResponse(contact);
    }

    private Contact getMapFromContactRequest(ContactRequest dto) {
        Integer logoId = dto.getLogoId();
        dto.setLogoId(null);
        Contact map = modelMapper.map(dto, Contact.class);
        if (logoId != null && !logoId.equals(0)) {
            cloudFileRepository.findById(logoId).ifPresent(map::setLogo);
        }
        return map;
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
    public List<FullContactResponse> reorder(@RequestBody List<ContactReorderDTO> dto) {
        List<Integer> ids = dto.stream().map(ContactReorderDTO::getId).collect(Collectors.toList());
        List<Integer> orders = dto.stream().map(ContactReorderDTO::getOrder).collect(Collectors.toList());
        return contactMapper.mapList(contactService.reorder(ids, orders));
    }

    @GetMapping("my")
    @PreAuthorize("isAuthenticated()")
    public List<FullContactResponse> getOfCurrentCard() {
        return contactMapper.mapList(contactService.getOfCurrentCard());
    }

}
