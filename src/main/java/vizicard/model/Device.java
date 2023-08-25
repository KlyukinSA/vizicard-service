package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Profile owner;

    @Column(length = 8, nullable = false, unique = true)
    private String url;

    public Device(Profile owner, String url) {
        this.owner = owner;
        this.url = url;
    }

}
