package vizicard.model.detail;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.Profile;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Education extends DetailBase {

    private String stage;
    private String institution;
    private String specialization;
    @Column(columnDefinition = "TIMESTAMP(0)")
    private Date graduationAt;

    public Education(Profile user) {
        super(user);
    }
}
