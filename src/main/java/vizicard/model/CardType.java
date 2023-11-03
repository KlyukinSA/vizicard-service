package vizicard.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class CardType {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;

    @Id
    @Column(columnDefinition = "ENUM('PERSON', 'COMPANY', 'COMMUNITY', 'GROUP')")
    @Enumerated(EnumType.STRING)
    private CardTypeEnum type;

    @Column(nullable = false)
    private String writing;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Card> cardsWhereType;

    public CardType(CardTypeEnum type, String writing) {
        this.type = type;
        this.writing = writing;
    }

}
