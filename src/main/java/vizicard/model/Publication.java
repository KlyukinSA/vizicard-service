package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(nullable = false)
    private Profile owner;

    @OneToOne
    private Profile profile;

    private String title;

    @Column(nullable = false)
    private String description;

    private Date date;

    @Column(columnDefinition = "TIMESTAMP(0) DEFAULT NOW()", nullable = false)
    private final Date createAt = new Date();

    public Publication(Profile owner) {
        this.owner = owner;
    }

}
