package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"account_owner_id", "card_owner_id", "card_id"}))
public class Relation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Account accountOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    private Card cardOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Card card;

    @OneToOne(fetch = FetchType.LAZY)
    private Card overlay;

    @Column(columnDefinition = "TIMESTAMP(0) DEFAULT NOW()", nullable = false)
    private final Date createAt = new Date();

    @Column(nullable = false)
    private boolean status = true;

    @Column(columnDefinition = "ENUM('EMPLOYEE', 'OWNER', 'SAVE', 'EXCHANGE', 'REFERRAL', 'REFERRER', 'REFERRER_LEVEL2')", nullable = false)
    @Enumerated(EnumType.STRING)
    private RelationType type;

    public Relation(Account ownerAccount, Card cardOwner, Card card, RelationType relationType) {
        this.accountOwner = ownerAccount;
        this.cardOwner = cardOwner;
        this.card = card;
        this.type = relationType;
    }

}
