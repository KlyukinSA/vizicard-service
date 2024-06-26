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

    @Column(columnDefinition = "ENUM('MEDIA', 'FILE', 'LINK')", nullable = false)
    @Enumerated(EnumType.STRING)
    private CloudFileType type;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Extension extension;

    @Column(length = 200)
    private String description;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false)
    private int quality;

    public CloudFile(String url, Album album, CloudFileType type, Extension extension, long size, int quality) {
        this.url = url;
        this.album = album;
        this.type = type;
        this.extension = extension;
        this.size = size;
        this.quality = quality;
    }

}
