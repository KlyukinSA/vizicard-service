package vizicard.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private Profile actor;

    @OneToOne
    @JoinColumn(nullable = false)
    private Profile page;

    @Column(columnDefinition = "TIMESTAMP(0) DEFAULT NOW()", nullable = false)
    private final Date createAt = new Date();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType type;

    public Action(Profile actor, Profile page, ActionType type) {
        this.actor = actor;
        this.page = page;
        this.type = type;
    }
}
