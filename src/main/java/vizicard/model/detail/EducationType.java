package vizicard.model.detail;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class EducationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "ENUM('PRIMARY', 'SECONDARY', 'HIGHER', 'VOCATIONAL')", nullable = false)
    @Enumerated(EnumType.STRING)
    private EducationLevel type;

    @Column(nullable = false)
    private String writing;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Education> educations;

    public EducationType(EducationLevel type, String writing) {
        this.type = type;
        this.writing = writing;
    }

}
