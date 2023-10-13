package vizicard.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vizicard.dto.contact.ContactResponse;
import vizicard.dto.publication.CommentResponse;
import vizicard.model.Contact;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContactMapper {
	public List<ContactResponse> map(List<Contact> contacts) {
		return contacts.stream()
				.filter(Contact::isStatus)
				.map((val) -> new ContactResponse(
						val.getId(),
						val.getType().getType(),
						val.getContact(),
						val.getTitle(),
						val.getDescription(),
						val.getOrder(),
						val.getType().getLogo().getUrl()))
				.collect(Collectors.toList());
	}
}
