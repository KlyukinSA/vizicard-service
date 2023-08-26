package vizicard.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import vizicard.dto.BriefResponseDTO;
import vizicard.model.Profile;
import vizicard.model.ProfileType;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {

  boolean existsByUsername(String username);

  Profile findByUsername(String username);

  @Transactional
  void deleteByUsername(String username);

    List<Profile> findAllByOwnerIdAndType(Integer id, ProfileType profileType);
}
