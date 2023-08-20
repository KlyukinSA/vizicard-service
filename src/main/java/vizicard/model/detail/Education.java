package vizicard.model.detail;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.Profile;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Profile owner;

    private String stage;
    private String institution;
    private String specialization;
    @Column(columnDefinition = "TIMESTAMP(0)")
    private Date graduationAt;

    @Column(nullable = false)
    private boolean status = true;

    public Education(Profile owner) {
        this.owner = owner;
    }
}
