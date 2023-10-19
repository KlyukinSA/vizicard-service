package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "card_id"}))
public class Relation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Account owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Card card;

    @Column(columnDefinition = "TIMESTAMP(0) DEFAULT NOW()", nullable = false)
    private final Date createAt = new Date();

    @Column(nullable = false)
    private boolean status = true;

    @Column(columnDefinition = "ENUM('OWNER', 'EDITOR', 'USUAL', 'EMPLOYEE', 'REFERRAL', 'REFERRER', 'SECONDARY', 'REFERRER_LEVEL2')", nullable = false)
    @Enumerated(EnumType.STRING)
    private RelationType type;

    public Relation(Account owner, Card card, RelationType relationType) {
        this.owner = owner;
        this.card = card;
        this.type = relationType;
    }

}
