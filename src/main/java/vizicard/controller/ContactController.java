package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.contact.*;
import vizicard.mapper.ContactMapper;
import vizicard.model.*;
import vizicard.repository.CloudFileRepository;
import vizicard.repository.ContactTypeRepository;
import vizicard.service.CardAttributeService;
import vizicard.service.ContactService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("cards/{cardAddress}/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactTypeRepository contactTypeRepository;
    private final CloudFileRepository cloudFileRepository;

    private final ContactService contactService;
    private final CardAttributeService cardAttributeService;

    private final ModelMapper modelMapper;
    private final ContactMapper contactMapper;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ContactResponse create(@PathVariable String cardAddress, @RequestBody ContactCreateDTO dto) {
        ContactType contactType = contactTypeRepository.findByType(dto.getType());
        Contact contact = getMapFromContactRequest(dto);
        contact.setType(contactType);

        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        contact = contactService.create(card, contact);

        return contactMapper.mapToResponse(contact);
    }

    @PutMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public FullContactResponse update(@PathVariable String cardAddress, @RequestBody ContactRequest dto, @PathVariable("id") Integer id) {
        Contact contact = getMapFromContactRequest(dto);
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        contact = contactService.update(card, contact, id);
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
    public void delete(@PathVariable String cardAddress, @PathVariable("id") Integer id) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        contactService.delete(card, id);
    }

    @PutMapping("order")
    @PreAuthorize("isAuthenticated()")
    public List<FullContactResponse> reorder(@PathVariable String cardAddress, @RequestBody List<ContactReorderDTO> dto) {
        List<Integer> ids = dto.stream().map(ContactReorderDTO::getId).collect(Collectors.toList());
        List<Integer> orders = dto.stream().map(ContactReorderDTO::getOrder).collect(Collectors.toList());
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        return contactMapper.mapList(contactService.reorder(card, ids, orders));
    }

    @GetMapping
    public List<FullContactResponse> getAllOfCard(@PathVariable String cardAddress) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        cardAttributeService.stopAccessToHiddenTab(TabTypeEnum.CONTACTS, card);
        return contactMapper.mapList(contactService.getAllOfCard(card));
    }

    @GetMapping("{id}")
    public FullContactResponse getById(@PathVariable String cardAddress, @PathVariable Integer id) {
        Card card = cardAttributeService.getCardByIdOrElseShortname(cardAddress);
        cardAttributeService.stopAccessToHiddenTab(TabTypeEnum.CONTACTS, card);
        return contactMapper.mapToResponse(contactService.findById(card, id));
    }

}
