package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"type_type", "card_owner_id"}))
public class Tab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private TabType type;
    @ManyToOne(fetch = FetchType.LAZY)
    private Card cardOwner;

    @Column(nullable = false)
    private boolean hidden;

    @Column(nullable = false, name = "`order`")
    private int order;

    public Tab(TabType type, Card cardOwner, boolean hidden, int order) {
        this.type = type;
        this.cardOwner = cardOwner;
        this.hidden = hidden;
        this.order = order;
    }
}
