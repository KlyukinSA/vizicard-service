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
public class Experience extends DetailBase {

    private String company;
    private String position;
    @Column(columnDefinition = "TIMESTAMP(0)")
    private Date startAt;
    @Column(columnDefinition = "TIMESTAMP(0)")
    private Date finishAt;
    private String tasks;

    public Experience(Profile user) {
        super(user);
    }
}
