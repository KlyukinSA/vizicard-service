package vizicard.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vizicard.dto.contact.ContactResponse;
import vizicard.model.Contact;
import vizicard.service.CloudFileService;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContactMapper {

	private final CloudFileService cloudFileService;

	public List<ContactResponse> mapList(List<Contact> contacts) {
		return contacts.stream()
				.filter(Contact::isStatus)
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	public ContactResponse mapToResponse(Contact contact) {
		return new ContactResponse(
				contact.getId(),
				contact.getType().getType(),
				contact.getContact(),
				contact.getTitle(),
				contact.getDescription(),
				contact.getOrder(),
				cloudFileService.findById(contact.getType().getLogo().getId()).getUrl());
	}

}
