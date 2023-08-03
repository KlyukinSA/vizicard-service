package vizicard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.ContactEnum;
import vizicard.model.ContactType;

import javax.persistence.Column;
import javax.persistence.OneToOne;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {

    private ContactEnum contactEnum;
    private String contact;

}
