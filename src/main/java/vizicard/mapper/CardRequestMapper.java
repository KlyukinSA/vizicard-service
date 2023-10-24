package vizicard.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vizicard.dto.profile.ProfileCreateDTO;
import vizicard.model.Card;
import vizicard.model.Relation;
import vizicard.model.RelationType;

@Component
@RequiredArgsConstructor
public class CardRequestMapper {
// TODO

//    private void mapCreate(ProfileCreateDTO dto, Card card) {
//        Card company = resolveCompany(dto)
//        if (dto.getCompanyId() != null) {
//            if (dto.getCompanyId().equals(0)) {
//                card.setCompany(null);
//            } else {
//                Card company = profileProvider.getTarget(dto.getCompanyId());
//                card.setCompany(company);
//
//                Relation relation = relationRepository.findByOwnerAndCard(card.getAccount(), company);
//                RelationType relationType;
//                if (relation != null) {
//                    relationType = relation.getType();
//                } else {
//                    relationType = RelationType.USUAL;
//                }
//                relator.relate(card.getAccount(), company, relationType);
//            }
//        }
//    }

}
