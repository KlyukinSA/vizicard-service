package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import vizicard.model.detail.Education;
import vizicard.model.detail.ProfileDetailStruct;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Data
@NoArgsConstructor
public class Profile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(unique = true, nullable = true, length = 50)
  private String username;
  @Column(nullable = true, length = 70)
  private String password;
  @Column(nullable = false)
  private Integer ownerId;

  @Column(nullable = false, length = 50)
  private String name;
  @Column(length = 140)
  private String title;
  @Column(columnDefinition = "TEXT")
  private String description;
  @Column(length = 50)
  private String city;
  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
  private List<Contact> contacts;

  @OneToOne
  private CloudFile avatar;
  @OneToOne
  private CloudFile background;

  @Column(columnDefinition = "TIMESTAMP(0) DEFAULT NOW()", nullable = false)
  private final Date createAt = new Date();

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProfileType profileType;

  @OneToOne
  private Profile company;

  @Column(nullable = false)
  private boolean status = true;

  @Column(columnDefinition = "TIMESTAMP(0)")
  private Date lastVizit;

  private ProfileDetailStruct detailStruct;

}
