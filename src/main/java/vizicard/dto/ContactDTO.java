package vizicard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.ContactEnum;
import vizicard.model.ContactType;

import javax.persistence.Column;
import javax.persistence.OneToOne;


@Data
@NoArgsConstructor
public class ContactDTO {

    private Integer contactTypeId;
    private String contact;

}
