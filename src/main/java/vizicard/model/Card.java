package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import vizicard.model.detail.ProfileDetailStruct;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Card {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Account account;

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
	private List<Contact> contacts = new ArrayList<>();

	@OneToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private CloudFile avatar;

	@Column(columnDefinition = "TIMESTAMP(0) DEFAULT NOW()", nullable = false)
	private final Date createAt = new Date();

	@Column(columnDefinition = "ENUM('USER', 'CUSTOM_USER', 'LEAD_USER', 'COMPANY', 'CUSTOM_COMPANY', 'LEAD_COMPANY', 'GROUP', 'WORKER')", nullable = false)
	@Enumerated(EnumType.STRING)
	private ProfileType type;

	@OneToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Card company;

	@Column(nullable = false)
	private boolean status = true;

	@OneToOne
	private Album album;

	private ProfileDetailStruct detailStruct;

	// SQL settings (OnDeleteAction.CASCADE)
	@OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Relation> relationsWhereCard;

	@OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Publication> publicationsWhereCard;

	@OneToMany(mappedBy = "referrer", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Shortname> shortnamesWhereReferrer;

	@OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Action> actionsWhereCard;

	@OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Shortname> shortnamesWhereCard;
}