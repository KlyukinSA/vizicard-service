package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class ContactType { // TODO GET all
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false) // TODO make as id
    private ContactEnum contactEnum;

    @OneToOne
    private CloudFile logo;

}
