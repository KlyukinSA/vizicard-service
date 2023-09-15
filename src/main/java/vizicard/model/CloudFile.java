package vizicard.model;

import com.amazonaws.services.appflow.model.PrefixFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class CloudFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String url;

    @ManyToOne
    private Profile owner;

    public CloudFile(String url, Profile owner) {
        this.url = url;
        this.owner = owner;
    }
}
