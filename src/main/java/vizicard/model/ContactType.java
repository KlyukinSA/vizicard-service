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
public class ContactType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "ENUM('PHONE', 'MAIL', 'SITE', 'FACEBOOK', 'INSTAGRAM', 'LINKEDIN', 'YOUTUBE', 'VK', 'TIKTOK', 'OK', 'TELEGRAM', 'WHATSAPP', 'VIBER')", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContactEnum type;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CloudFile logo;

    @Column(nullable = false)
    private String writing;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Contact> contacts;

    @ManyToOne
    @JoinColumn(nullable = false)
    private ContactGroup group;

    @Column(nullable = false)
    private String urlBase;

}
