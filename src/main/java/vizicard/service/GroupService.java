package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vizicard.dto.profile.response.BriefCardResponse;
import vizicard.exception.CustomException;
import vizicard.model.*;
import vizicard.repository.RelationRepository;
import vizicard.mapper.CardMapper;
import vizicard.utils.ProfileProvider;
import vizicard.utils.RelationValidator;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final ProfileProvider profileProvider;
    private final RelationRepository relationRepository;
    private final CardMapper cardMapper;
    private final RelationValidator relationValidator;

    public List<BriefCardResponse> getAllGroupMembers(Integer groupId) {
        Card group = profileProvider.getTarget(groupId);
        letGroupPass(group);

        return relationRepository.findAllByCardAndType(group, RelationType.MEMBER).stream()
                .filter(Relation::isStatus)
                .map(Relation::getCardOwner)
                .filter(Card::isStatus)
                .map(cardMapper::mapToBrief)
                .collect(Collectors.toList());
    }

     private void letGroupPass(Card card) {
        if (card.getType() != CardType.GROUP) {
            throw new CustomException("This profile should be a group", HttpStatus.FORBIDDEN);
        }
    }

    public List<BriefCardResponse> addGroupMembers(Integer groupId, List<Integer> memberIds) {
        Card group = profileProvider.getTarget(groupId);
        letGroupPass(group);
        relationValidator.stopNotOwnerOf(group);

        for (Integer memberId : memberIds) {
            Card card = profileProvider.getTarget(memberId);
            Relation friendship = relationRepository.findByAccountOwnerAndCard(profileProvider.getUserFromAuth(), card);
            if (friendship != null) {
                Relation membership = relationRepository.findByCardOwnerAndCardTypeAndType(card, CardType.GROUP, RelationType.MEMBER);
                if (membership != null && membership.getCard().getId().equals(groupId)) {
                    membership.setStatus(true);
                } else {
                    membership = new Relation(card.getAccount(), card, group, RelationType.MEMBER);
                }
                relationRepository.save(membership);
            }
        }
        return getAllGroupMembers(groupId);
    }

    public List<BriefCardResponse> getAllMyGroups() {
        Account user = profileProvider.getUserFromAuth();
        return relationRepository.findAllByAccountOwnerAndCardTypeAndType(user, CardType.GROUP, RelationType.OWNER).stream()
                .map(Relation::getCard)
                .filter(Card::isStatus)
                .map(cardMapper::mapToBrief)
                .collect(Collectors.toList());
    }

}
