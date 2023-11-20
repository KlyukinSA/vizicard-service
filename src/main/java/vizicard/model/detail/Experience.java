package vizicard.model.detail;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.Card;
import vizicard.model.CardAttribute;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Experience extends CardAttribute {

    @Column(nullable = false)
    private String company;
    @Column(nullable = false)
    private String position;
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startAt;
    @Temporal(TemporalType.DATE)
    private Date finishAt;
    @Column(nullable = false)
    private String tasks;

    public Experience(Card user) {
        super(user);
    }
}
