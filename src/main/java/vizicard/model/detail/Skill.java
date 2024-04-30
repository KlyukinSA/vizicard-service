package vizicard.model.detail;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.Card;
import vizicard.model.CardAttribute;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Data
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"card_owner_id", "value"}))
public class Skill extends CardAttribute {

    @Column(length = 80)
    private String value;

    public Skill(Card owner, String value) {
        super(owner);
        this.value = value;
    }

}
