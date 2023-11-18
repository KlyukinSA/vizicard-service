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

//	@OneToOne
//  ignored         	 @JoinColumn(foreignKey = @javax.persistence.ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
//  this sets RESTRICT   @OnDelete(action = OnDeleteAction.NO_ACTION)
//	private CloudFile avatar;

	private Integer avatarId;
	private Integer backgroundId;

	@Column(columnDefinition = "TIMESTAMP(0) DEFAULT NOW()", nullable = false)
	private final Date createAt = new Date();

	@ManyToOne
	@JoinColumn(nullable = false)
	private CardType type;

	@Column(nullable = false)
	private boolean custom;

	@Column(nullable = false)
	private boolean status = true;

	@OneToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Album album;

	private ProfileDetailStruct detailStruct;

	private String cardName;

	// SQL settings (OnDeleteAction.CASCADE)
	@OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Relation> relationsWhereCard;
	@OneToMany(mappedBy = "cardOwner", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Relation> relationsWhereCardOwner;

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

	@OneToMany(mappedBy = "cardOwner", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Tab> tabs;

}
