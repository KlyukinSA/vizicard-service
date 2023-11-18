package vizicard.model.detail;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.Card;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Data
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"card_owner_id", "skill"}))
public class Skill extends DetailBase {

    private String skill;

    public Skill(Card owner, String skill) {
        super(owner);
        this.skill = skill;
    }

}
