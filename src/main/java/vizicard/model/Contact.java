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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"card_owner_id", "order"}))
public class Contact extends CardAttribute {

    @ManyToOne
    @JoinColumn(nullable = false)
    private ContactType type;

    @Column(nullable = false, length = 200)
    private String value;

    private String title;

    private String description;

    @Column(name = "`order`")
    private Integer order;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CloudFile logo;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Action> clicks;

}
