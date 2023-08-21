package vizicard.model.detail;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.Profile;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;
@Entity
@Data
@NoArgsConstructor
public class Skill extends DetailBase {

    private String skill;

    public Skill(Profile user) {
        super(user);
    }

}
