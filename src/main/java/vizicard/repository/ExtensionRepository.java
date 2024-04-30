package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.Extension;

public interface ExtensionRepository extends JpaRepository<Extension, Integer> {
    Extension findByName(String upperCase);
}
