package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "type_id"}))
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Profile owner;

    @OneToOne
    @JoinColumn(nullable = false)
    private ContactType type;

    @Column(nullable = false, length = 200)
    private String contact;

}
