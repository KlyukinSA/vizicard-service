package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "cloud") // wtf
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

    @Column(columnDefinition = "ENUM('MEDIA', 'FILE')", nullable = false)
    @Enumerated(EnumType.STRING)
    private CloudFileType type;

    @ManyToOne
    private Extension extension;

    @Column(length = 200)
    private String description;

    public CloudFile(String url, Album album, CloudFileType type, Extension extension) {
        this.url = url;
        this.album = album;
        this.type = type;
        this.extension = extension;
    }

}
