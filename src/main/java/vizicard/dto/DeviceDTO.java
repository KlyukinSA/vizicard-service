package vizicard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.Profile;

import javax.persistence.Column;
import javax.persistence.OneToOne;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDTO {

    private Integer id;

    private Integer ownerId;

    private String url;

}
