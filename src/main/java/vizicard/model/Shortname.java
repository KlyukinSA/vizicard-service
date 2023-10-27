package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Shortname {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;

    @Column(nullable = false, unique = true)
    private String shortname;

    @Column(columnDefinition = "ENUM('MAIN', 'DEVICE', 'USUAL')", nullable = false)
    @Enumerated(EnumType.STRING)
    private ShortnameType type;

    @ManyToOne(fetch = FetchType.LAZY)
    private Card referrer;

    @OneToMany(mappedBy = "shortname", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Action> visits;

    public Shortname(Card card, String shortname, ShortnameType shortnameType) {
        this.card = card;
        this.shortname = shortname;
        this.type = shortnameType;
    }

}
