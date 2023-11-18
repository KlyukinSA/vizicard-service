package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vizicard.model.TabType;
import vizicard.model.TabTypeEnum;

public interface TabTypeRepository extends JpaRepository<TabType, Integer> {
    TabType findByType(TabTypeEnum type);
}
