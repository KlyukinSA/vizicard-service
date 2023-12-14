package vizicard.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vizicard.dto.contact.*;
import vizicard.model.*;
import vizicard.service.CloudFileService;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContactMapper {

	private final CloudFileService cloudFileService;
	private final ModelMapper modelMapper;

	public List<FullContactResponse> mapList(List<Contact> contacts) {
		return contacts.stream()
				.filter(Contact::isStatus)
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	public FullContactResponse mapToResponse(Contact contact) {
		FullContactResponse res = modelMapper.map(mapToBrief(contact), FullContactResponse.class);
		res.setDescription(contact.getDescription());
		return res;
	}

	public ContactResponse mapToBrief(Contact contact) {
		CloudFile logo = contact.getLogo();
		Integer logoId;
		if (logo != null) {
			logoId = logo.getId();
		} else {
			logoId = contact.getType().getLogo().getId();
		}
		return new ContactResponse(
				contact.getIndividualId(),
				contact.getType().getType(),
				contact.getValue(),
				contact.getTitle(),
				contact.getOrder(),
				cloudFileService.findById(logoId).getUrl());
	}

	public ContactTypeResponse mapToContactTypeResponse(ContactType contactType) {
		ContactTypeResponse map = modelMapper.map(contactType, ContactTypeResponse.class);
		map.setLogoUrl(cloudFileService.findById(contactType.getLogo().getId()).getUrl());
		map.setRegexp(contactType.getRegex());
		return map;
	}

	public ContactGroupResponse mapToContactGroupResponse(ContactGroup contactGroup) {
		ContactGroupResponse map = modelMapper.map(contactGroup, ContactGroupResponse.class);
		map.setContactTypes(contactGroup.getContactTypes().stream()
				.map(this::mapToContactTypeResponse)
				.collect(Collectors.toList()));
		return map;
	}
}
