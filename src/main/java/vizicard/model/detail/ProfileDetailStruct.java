package vizicard.model.detail;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.List;

@Embeddable
@Data
public class ProfileDetailStruct {
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Education> education;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Experience> experience;

}
