package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Shortname {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Profile owner;

    @Column(nullable = false, unique = true)
    private String shortname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShortnameType type;

    public Shortname(Profile owner, String shortname, ShortnameType shortnameType) {
        this.owner = owner;
        this.shortname = shortname;
        this.type = shortnameType;
    }

}
