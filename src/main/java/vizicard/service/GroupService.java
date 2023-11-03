package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vizicard.dto.GroupMemberStatusListResponseDTO;
import vizicard.dto.profile.response.BriefCardResponse;
import vizicard.exception.CustomException;
import vizicard.model.*;
import vizicard.repository.GroupMemberStatusRepository;
import vizicard.repository.RelationRepository;
import vizicard.mapper.CardMapper;
import vizicard.utils.ProfileProvider;
import vizicard.utils.RelationValidator;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final RelationRepository relationRepository;
    private final GroupMemberStatusRepository groupMemberStatusRepository;

    private final ProfileProvider profileProvider;
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
        if (card.getType().getType() != CardTypeEnum.GROUP) {
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
                Relation membership = relationRepository.findByCardOwnerAndCardTypeTypeAndType(card, CardTypeEnum.GROUP, RelationType.MEMBER);
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
        return relationRepository.findAllByAccountOwnerAndCardTypeTypeAndType(user, CardTypeEnum.GROUP, RelationType.OWNER).stream()
                .map(Relation::getCard)
                .filter(Card::isStatus)
                .map(cardMapper::mapToBrief)
                .collect(Collectors.toList());
    }

    public GroupMemberStatus createStatus(Integer groupId, String name) {
        Card group = profileProvider.getTarget(groupId);
        letGroupPass(group);
        relationValidator.stopNotOwnerOf(group);

        GroupMemberStatus status = new GroupMemberStatus();
        status.setGroup(group);
        status.setName(name);
        return groupMemberStatusRepository.save(status);
    }

    public GroupMemberStatus changeMemberStatus(Integer groupId, Integer memberId, Integer statusId) {
        Card group = profileProvider.getTarget(groupId);
        letGroupPass(group);
        relationValidator.stopNotOwnerOf(group);

        Card member = profileProvider.getTarget(memberId);
        Relation membership = relationRepository.findByCardOwnerAndCard(member, group);
        if (membership == null || membership.getType() != RelationType.MEMBER) {
            throw new CustomException("he is not a member", HttpStatus.BAD_REQUEST);
        }

        GroupMemberStatus status = groupMemberStatusRepository.findById(statusId).get();
        if (!Objects.equals(status.getGroup().getId(), group.getId())) {
            throw new CustomException("status not of this group", HttpStatus.BAD_REQUEST);
        }

        membership.setGroupStatus(status);
        return relationRepository.save(membership).getGroupStatus();
    }


    public List<GroupMemberStatusListResponseDTO> getAllStatusesWithTheirMembers(Integer groupId) {
        Card group = profileProvider.getTarget(groupId);
        letGroupPass(group);
        relationValidator.stopNotOwnerOf(group);

        List<GroupMemberStatus> allByGroup = groupMemberStatusRepository.findAllByGroup(group);
        return allByGroup.stream()
                .map(s -> new GroupMemberStatusListResponseDTO(s.getId(), s.getName(),
                        relationRepository.findAllByCardAndTypeAndGroupStatus(group, RelationType.MEMBER, s).stream()
                                .filter(Relation::isStatus)
                                .map(Relation::getCardOwner)
                                .filter(Card::isStatus)
                                .map(cardMapper::mapToBrief)
                                .collect(Collectors.toList())
                        ))
                .collect(Collectors.toList());
    }

}
