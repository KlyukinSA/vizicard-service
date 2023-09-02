package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import vizicard.model.detail.ProfileDetailStruct;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

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

  @Column(nullable = false, length = 50)
  private String name;
  @Column(length = 140)
  private String title;
  @Column(columnDefinition = "TEXT")
  private String description;
  @Column(length = 50)
  private String city;
  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<Contact> contacts;

  @OneToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  private CloudFile avatar;

  @Column(columnDefinition = "TIMESTAMP(0) DEFAULT NOW()", nullable = false)
  private final Date createAt = new Date();

  @Column(columnDefinition = "ENUM('USER', 'COMPANY', 'CUSTOM', 'GROUP', 'LEAD_USER', 'LEAD_COMPANY')", nullable = false)
  @Enumerated(EnumType.STRING)
  private ProfileType type;

  @OneToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Profile company;

  @Column(nullable = false)
  private boolean status = true;

  @Column(columnDefinition = "TIMESTAMP(0)", nullable = false)
  private Date lastVizit = new Date();

  private ProfileDetailStruct detailStruct;

  // SQL settings (OnDeleteAction.CASCADE)
  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<Relation> relationsWhereOwner;
  @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<Relation> relationsWhereProfile;

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<Publication> publicationsWhereOwner;
  @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<Publication> publicationsWhereProfile;

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<Shortname> shortnames;

  @OneToMany(mappedBy = "actor", cascade = CascadeType.ALL)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<Action> actionsWhereActor;
  @OneToMany(mappedBy = "page", cascade = CascadeType.ALL)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<Action> actionsWherePage;

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<CloudFile> files;

}
