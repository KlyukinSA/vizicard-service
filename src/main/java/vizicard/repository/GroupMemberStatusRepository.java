package vizicard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vizicard.model.Card;
import vizicard.model.GroupMemberStatus;

import java.util.List;

public interface GroupMemberStatusRepository extends JpaRepository<GroupMemberStatus, Integer> {
    List<GroupMemberStatus> findAllByGroup(Card group);
}
