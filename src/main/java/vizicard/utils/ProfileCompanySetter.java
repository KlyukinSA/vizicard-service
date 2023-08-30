package vizicard.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vizicard.model.Profile;
import vizicard.model.Relation;
import vizicard.model.RelationType;
import vizicard.repository.RelationRepository;

@Component
@RequiredArgsConstructor
public class ProfileCompanySetter {

    private final RelationRepository relationRepository;

    public Profile addRelation(Profile member, Profile company) {
        Relation relation = relationRepository.findByOwnerAndProfile(member, company);
        if (relation == null || !relation.isStatus()) {
            relationRepository.save(new Relation(member, company, RelationType.USUAL));
        }
        return member;
    }

    public Profile setCompany(Profile member, Profile company) {
        member.setCompany(company);
        return addRelation(member, company);
    }

}
