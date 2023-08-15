package vizicard.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private Profile actor;

    @OneToOne
    @JoinColumn(nullable = false)
    private Profile page;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant createAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType type;

}
