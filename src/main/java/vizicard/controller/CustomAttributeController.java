package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.CustomAttributeDTO;
import vizicard.exception.CustomException;
import vizicard.model.Account;
import vizicard.model.Card;
import vizicard.model.CustomAttribute;
import vizicard.model.Relation;
import vizicard.repository.CardRepository;
import vizicard.repository.CustomAttributeRepository;
import vizicard.repository.RelationRepository;
import vizicard.utils.ProfileProvider;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cards/{id}/custom-attributes")
@RequiredArgsConstructor
public class CustomAttributeController {

    private final CustomAttributeRepository customAttributeRepository;
    private final CardRepository cardRepository;
    private final ModelMapper modelMapper;
    private final ProfileProvider profileProvider;
    private final RelationRepository relationRepository;

    @PutMapping
    public CustomAttributeDTO createOrUpdateOrDelete(@PathVariable("id") Integer cardId, @RequestBody CustomAttributeDTO dto) {
        Card card = cardRepository.findById(cardId).get();
        CustomAttribute map = modelMapper.map(dto, CustomAttribute.class);
        Account user = profileProvider.getUserFromAuth();
        Relation relation = relationRepository.findByAccountOwnerAndCard(user, card);
        if (relation == null) {
            throw new CustomException("you are not related to this card", HttpStatus.BAD_REQUEST);
        }
        CustomAttribute attribute = customAttributeRepository.findByRelationAndName(relation, map.getName());
        if (attribute == null) {
            attribute = new CustomAttribute(null, map.getName(), map.getValue(), relation);
        } else {
            attribute.setValue(map.getValue());
        }
        return modelMapper.map(customAttributeRepository.save(attribute), CustomAttributeDTO.class);
    }

    @GetMapping
    public List<CustomAttributeDTO> getAll(@PathVariable("id") Integer cardId) {
        Card card = cardRepository.findById(cardId).get();
        Account user = profileProvider.getUserFromAuth();
        Relation relation = relationRepository.findByAccountOwnerAndCard(user, card);
        return relation.getCustomAttributes().stream()
                .filter(c -> c.getValue() != null && !c.getValue().isEmpty())
                .map(c -> modelMapper.map(c, CustomAttributeDTO.class))
                .collect(Collectors.toList());
    }

}
