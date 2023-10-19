package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Card card;

    @Column(columnDefinition = "TIMESTAMP(0) DEFAULT NOW()", nullable = false)
    private final Date createAt = new Date();

    @Column(columnDefinition = "ENUM('VIZIT', 'SAVE', 'CLICK', 'GIVE_BONUS')", nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType type;

    private float bonus;

    @ManyToOne(fetch = FetchType.LAZY)
    private Contact resource;

    @Column(nullable = false)
    private String ip;

    @ManyToOne(fetch = FetchType.LAZY)
    private Shortname shortname;

    public Action(Account owner, Card card, ActionType type, String ip) {
        this.owner = owner;
        this.card = card;
        this.type = type;
        this.ip = ip;
    }

}
