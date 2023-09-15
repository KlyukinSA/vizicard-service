package vizicard.model.detail;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.ContactType;
import vizicard.model.Profile;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Education extends DetailBase {

    @Column(nullable = false)
    private String institution;

    @Column(nullable = false)
    private String specialization;

    @Column(nullable = false)
    private Short graduationAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private EducationType type;

    public Education(Profile user) {
        super(user);
    }
}
