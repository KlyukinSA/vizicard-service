package vizicard.model.detail;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.Card;
import vizicard.model.CardAttribute;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Education extends CardAttribute {

    @Column(nullable = false)
    private String institution;

    @Column(nullable = false)
    private String specialization;

    @Column(nullable = false)
    private Short graduationAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private EducationType type;

    public Education(Card user) {
        super(user);
    }
}
