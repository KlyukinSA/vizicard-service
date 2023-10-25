package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vizicard.dto.BriefCardResponse;
import vizicard.exception.CustomException;
import vizicard.model.*;
import vizicard.repository.RelationRepository;
import vizicard.mapper.CardMapper;
import vizicard.utils.ProfileProvider;
import vizicard.utils.RelationValidator;
import vizicard.utils.Relator;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final ProfileProvider profileProvider;
    private final RelationRepository relationRepository;
    private final CardMapper cardMapper;
    private final RelationValidator relationValidator;
    private final Relator relator;

    public List<BriefCardResponse> getAllGroupMembers(Integer groupId) {
        Card group = profileProvider.getTarget(groupId);
        letGroupPass(group);
        Integer ownerId = relationRepository.findByTypeAndCard(RelationType.OWNER, group).getAccountOwner().getId();

        return relationRepository.findAllByCard(group).stream()
                .filter(Relation::isStatus)
                .map(Relation::getAccountOwner)
                .filter(Account::isStatus)
                .filter(acc -> !Objects.equals(acc.getId(), ownerId))
                .map(Account::getCurrentCard)
                .filter(Card::isStatus)
                .map(cardMapper::mapToBrief)
                .collect(Collectors.toList());
    }

     private void letGroupPass(Card card) {
        if (card.getType() != CardType.ROOM) {
            throw new CustomException("This profile should be a group", HttpStatus.FORBIDDEN);
        }
    }

    public void addGroupMembers(Integer groupId, List<Integer> memberIds) {
        Card group = profileProvider.getTarget(groupId);
        letGroupPass(group);
        relationValidator.stopNotOwnerOf(group);

        for (Integer memberId : memberIds) {
            Card card = profileProvider.getTarget(memberId);
            relator.relate(card.getAccount(), card, group, RelationType.USUAL);
        }
    }

    public List<BriefCardResponse> getAllMyGroups() {
        Account user = profileProvider.getUserFromAuth();
        return relationRepository.findAllByAccountOwnerAndCardType(user, CardType.ROOM).stream()
                .map(Relation::getCard)
                .filter(Card::isStatus)
                .map(cardMapper::mapToBrief)
                .collect(Collectors.toList());
    }

}
