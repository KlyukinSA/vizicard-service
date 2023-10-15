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

    @Column(nullable = false)
    private String url;

    @ManyToOne(targetEntity = Album.class)
    @JoinColumn(nullable = false)
    private Album album;

    @Column(nullable = false)
    private boolean status = true;

    public CloudFile(String url, Album album) {
        this.url = url;
        this.album = album;
    }

}
