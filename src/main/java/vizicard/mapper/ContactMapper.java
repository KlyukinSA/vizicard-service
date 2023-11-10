package vizicard.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vizicard.dto.contact.ContactListResponse;
import vizicard.dto.contact.ContactResponse;
import vizicard.dto.contact.FullContactResponse;
import vizicard.model.CloudFile;
import vizicard.model.Contact;
import vizicard.service.CloudFileService;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ContactMapper {

	private final CloudFileService cloudFileService;
	private final ModelMapper modelMapper;

	public ContactListResponse mapList(List<Contact> contacts) {
		List<FullContactResponse> full = new ArrayList<>();
		List<ContactResponse> brief = new ArrayList<>();
		contacts.stream()
				.filter(Contact::isStatus)
				.forEach((c) -> {
					if (c.isFull()) {
						full.add(mapToResponse(c));
					} else {
						brief.add(mapToBrief(c));
					}
				});
		return new ContactListResponse(full, brief);
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
				contact.getId(),
				contact.getType().getType(),
				formContactUrl(contact),
				contact.getTitle(),
				contact.getOrder(),
				cloudFileService.findById(logoId).getUrl());
	}

	private String formContactUrl(Contact contact) {
		String s = contact.getContact();
		if (s.contains(".")) {
			return s;
		}
		return contact.getType().getUrlBase() + contact.getContact();
	}
}
