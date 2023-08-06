package vizicard.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Data // Create getters and setters
@NoArgsConstructor
public class Profile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

//  @Size(min = 4, max = 255, message = "Minimum username length: 4 characters")
  @Column(unique = true, nullable = false, length = 50)
  private String username;

//  @Size(min = 8, message = "Minimum password length: 8 characters")
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

// Добавление полей avatar_id и background_id в таблицу profiles, ссылающиеся на таблицу files

  @OneToOne
  private CloudFile avatar;

  @OneToOne
  private CloudFile background;

//  @ElementCollection(fetch = FetchType.EAGER)
//  List<AppUserRole> appUserRoles;

}
