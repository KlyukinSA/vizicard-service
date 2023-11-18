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
public class TabType {

    @Id
    @Column(columnDefinition = "ENUM('CONTACTS', 'RESUME', 'MEDIA', 'FILE')")
    @Enumerated(EnumType.STRING)
    private TabTypeEnum type;

    @Column(nullable = false)
    private String writing;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Tab> tabsWhereType;

    public TabType(TabTypeEnum type, String writing) {
        this.type = type;
        this.writing = writing;
    }

}
