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
import vizicard.repository.CustomContactTypeRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("contacts/types")
@RequiredArgsConstructor
public class ContactTypeController {

    private final ContactTypeRepository contactTypeRepository;
    private final ContactMapper contactMapper;
    private final CustomContactTypeRepository customContactTypeRepository;
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
