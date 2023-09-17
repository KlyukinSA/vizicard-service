package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "order"}))
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Profile owner;

    @ManyToOne
    @JoinColumn(nullable = false)
    private ContactType type;

    @Column(nullable = false, length = 200)
    private String contact;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "`order`")
    private Integer order;

    @Column(nullable = false)
    private boolean status = true;

}
