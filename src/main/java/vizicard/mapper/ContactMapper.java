package vizicard.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vizicard.dto.contact.ContactResponse;
import vizicard.model.CloudFile;
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
		CloudFile logo = contact.getLogo();
		Integer logoId;
		if (logo != null) {
			logoId = logo.getId();
		} else {
			logoId = contact.getType().getLogo().getId();
		}
		return new ContactResponse(
				contact.getId(),
				contact.getType().getType(),
				contact.getContact(),
				contact.getTitle(),
				contact.getDescription(),
				contact.getOrder(),
				cloudFileService.findById(logoId).getUrl());
	}

}
