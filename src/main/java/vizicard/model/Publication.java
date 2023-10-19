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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Account owner;

    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;

    private String title;

    @Column(nullable = false)
    private String description;

    private Date date;

    private Float rating;
    private boolean moderated = false;

    @Column(columnDefinition = "TIMESTAMP(0) DEFAULT NOW()", nullable = false)
    private final Date createAt = new Date();

    public Publication(Account owner) {
        this.owner = owner;
    }

}
