package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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

    @OneToOne(fetch = FetchType.LAZY)
    private GroupMemberStatus groupStatus;

    @Column(columnDefinition = "TIMESTAMP(0) DEFAULT NOW()", nullable = false)
    private final Date createAt = new Date();

    @Column(nullable = false)
    private boolean status = true;

    @Column(columnDefinition = "ENUM('EMPLOYEE', 'OWNER', 'SAVE', 'EXCHANGE', 'REFERRAL', 'REFERRER', 'REFERRER_LEVEL2', 'MEMBER')", nullable = false)
    @Enumerated(EnumType.STRING)
    private RelationType type;

    @OneToMany(mappedBy = "relation", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<CustomAttribute> customAttributes;

    public Relation(Account ownerAccount, Card cardOwner, Card card, RelationType relationType) {
        this.accountOwner = ownerAccount;
        this.cardOwner = cardOwner;
        this.card = card;
        this.type = relationType;
    }

}
