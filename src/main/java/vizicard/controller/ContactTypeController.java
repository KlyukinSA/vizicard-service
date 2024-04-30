package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.contact.ContactGroupResponse;
import vizicard.dto.contact.ContactTypeResponse;
import vizicard.mapper.ContactMapper;
import vizicard.model.ContactEnum;
import vizicard.model.ContactGroup;
import vizicard.model.ContactType;
import vizicard.repository.ContactGroupRepository;
import vizicard.repository.ContactTypeRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("contacts/types")
@RequiredArgsConstructor
public class ContactTypeController {

    private final ContactTypeRepository contactTypeRepository;
    private final ContactMapper contactMapper;
    private final ContactGroupRepository contactGroupRepository;

    @GetMapping
    public List<ContactTypeResponse> getAllTypes() {
        return contactTypeRepository.findAll().stream()
                .map(contactMapper::mapToContactTypeResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("{type}")
    public ContactTypeResponse getTypeByType(@PathVariable ContactEnum type) {
        return contactMapper.mapToContactTypeResponse(contactTypeRepository.findByType(type));
    }

    @GetMapping("search")
    public List<ContactGroupResponse> searchLike(String typeOrGroupWriting) {
        List<ContactType> types = contactTypeRepository.findAllByWritingLike("%" + typeOrGroupWriting + "%");
        if (!types.isEmpty()) {
            return contactGroupRepository.findAll().stream()
                    .filter(g -> types.stream().anyMatch(t -> t.getGroups().stream().map(ContactGroup::getId).collect(Collectors.toSet()).contains(g.getId())))
                    .map(g -> new ContactGroupResponse(g.getType(), g.getWriting(), getTypeResponsesByGroup(g, types)))
                    .collect(Collectors.toList());
        } else {
            return contactGroupRepository.findAllByWritingLike("%" + typeOrGroupWriting + "%").stream()
                    .map(contactMapper::mapToContactGroupResponse)
                    .collect(Collectors.toList());
        }
    }

    private List<ContactTypeResponse> getTypeResponsesByGroup(ContactGroup group, List<ContactType> types) {
        return types.stream()
                .filter(t -> t.getGroups().stream().map(ContactGroup::getId).collect(Collectors.toSet()).contains(group.getId()))
                .map(contactMapper::mapToContactTypeResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("groups")
    public List<ContactGroupResponse> getAllGroups() {
        return contactGroupRepository.findAll().stream()
                .map(contactMapper::mapToContactGroupResponse)
                .collect(Collectors.toList());
    }

}
