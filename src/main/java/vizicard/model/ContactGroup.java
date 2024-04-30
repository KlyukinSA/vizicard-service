package vizicard.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "ENUM('SOCIAL', 'MUSIC', 'PAYMENT')", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContactGroupEnum type;

    @Column(nullable = false)
    private String writing;

    @ManyToMany(mappedBy = "groups", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ContactType> contactTypes;

}
