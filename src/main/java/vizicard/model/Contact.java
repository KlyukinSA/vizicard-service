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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "order"}))
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Card owner;

    @ManyToOne
    @JoinColumn(nullable = false)
    private ContactType type;

    @Column(nullable = false, length = 200)
    private String contact;

    private String title;

    private String description;

    @Column(name = "`order`")
    private Integer order;

    @Column(nullable = false)
    private boolean status = true;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CloudFile logo;

    @Column(nullable = false)
    private boolean full;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Action> clicks;

}
