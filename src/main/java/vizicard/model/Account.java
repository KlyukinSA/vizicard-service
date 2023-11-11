package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(unique = true, nullable = true, length = 50)
	private String username;
	@Column(nullable = true, length = 70)
	private String password;

	@Column(columnDefinition = "TIMESTAMP(0)", nullable = false)
	private Date lastVizit = new Date();

	@Column(nullable = false)
	private boolean status = true;

	private float cash = 0;
	private float referralBonus = 0;

	@OneToOne
	@JoinColumn(nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Card currentCard;

	@OneToOne
	@JoinColumn(nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Card mainCard;

	@OneToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Card employer;

	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Card> cardsWhereAccount;

	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Shortname> shortnamesWhereAccount;

	@OneToMany(mappedBy = "accountOwner", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Relation> relationsWhereAccountOwner;

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Action> actionsWhereOwner;

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Publication> publicationsWhereOwner;

	public Account(String username, String password) {
		this.username = username;
		this.password =	password;
	}

}
