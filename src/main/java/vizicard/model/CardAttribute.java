package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@MappedSuperclass
@Data
@NoArgsConstructor
public class CardAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Card cardOwner;

    @Column(nullable = false)
    private boolean status = true;

    public CardAttribute(Card cardOwner) {
        this.cardOwner = cardOwner;
    }

}
