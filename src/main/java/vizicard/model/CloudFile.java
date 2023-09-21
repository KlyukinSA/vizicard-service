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

    @ManyToOne(targetEntity = Album.class)
    @JoinColumn(nullable = false)
    private Album album;

    public CloudFile(String url, Album album) {
        this.url = url;
        this.album = album;
    }

}
