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
    private Profile owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Profile profile;

    @Column(columnDefinition = "TIMESTAMP(0) DEFAULT NOW()", nullable = false)
    private final Date createAt = new Date();

    @Column(columnDefinition = "ENUM('VIZIT', 'SAVE', 'CLICK', 'GIVE_BONUS')", nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType type;

    private float bonus;

    public Action(Profile owner, Profile profile, ActionType type) {
        this.owner = owner;
        this.profile = profile;
        this.type = type;
    }
}
