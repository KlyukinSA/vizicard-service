package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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

  @Column(unique = true, nullable = false, length = 50)
  private String username;

  @Column(nullable = false, length = 70)
  private String password;

  @Column(nullable = false, length = 50)
  private String name;

  @Column(length = 140)
  private String position;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(length = 50)
  private String company;

  @Column(length = 50)
  private String city;

  @OneToOne
  private CloudFile avatar;

  @OneToOne
  private CloudFile background;

  @Column(columnDefinition = "TIMESTAMP(0) DEFAULT NOW()", nullable = false)
  private final Date createAt = new Date();

}
