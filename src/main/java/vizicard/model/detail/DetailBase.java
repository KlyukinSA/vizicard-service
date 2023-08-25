package vizicard.model.detail;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.Profile;

import javax.persistence.*;

@MappedSuperclass
@Data
@NoArgsConstructor
public class DetailBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Profile owner;

    @Column(nullable = false)
    private boolean status = true;

    public DetailBase(Profile owner) {
        this.owner = owner;
    }

}
