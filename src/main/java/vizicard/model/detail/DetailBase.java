package vizicard.model.detail;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.Card;

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
    private Card cardOwner;

    @Column(nullable = false)
    private boolean status = true;

    public DetailBase(Card cardOwner) {
        this.cardOwner = cardOwner;
    }

}
