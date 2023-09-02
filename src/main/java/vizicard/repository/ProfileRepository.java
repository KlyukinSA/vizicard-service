package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vizicard.model.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {

  boolean existsByUsername(String username);

  Profile findByUsername(String username);
}
