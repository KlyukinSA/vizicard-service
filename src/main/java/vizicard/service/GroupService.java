package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vizicard.dto.BriefProfileResponseDTO;
import vizicard.exception.CustomException;
import vizicard.model.Profile;
import vizicard.model.ProfileType;
import vizicard.model.Relation;
import vizicard.model.RelationType;
import vizicard.repository.RelationRepository;
import vizicard.mapper.ProfileMapper;
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
    private final ProfileMapper profileMapper;
    private final RelationValidator relationValidator;
    private final Relator relator;

    public List<BriefProfileResponseDTO> getAllGroupMembers(Integer groupId) {
        Profile group = profileProvider.getTarget(groupId);
        letGroupPass(group);
        Integer ownerId = relationRepository.findByTypeAndProfile(RelationType.OWNER, group).getOwner().getId();

        return relationRepository.findAllByProfile(group).stream()
                .filter(Relation::isStatus)
                .map(Relation::getOwner)
                .filter(Profile::isStatus)
                .filter((val) -> !Objects.equals(val.getId(), ownerId))
                .map(profileMapper::mapToBrief)
                .collect(Collectors.toList());
    }

     private void letGroupPass(Profile group) {
        if (group.getType() != ProfileType.GROUP) {
            throw new CustomException("This profile should be a group", HttpStatus.FORBIDDEN);
        }
    }

    public void addGroupMembers(Integer groupId, List<Integer> memberIds) {
        Profile group = profileProvider.getTarget(groupId);
        letGroupPass(group);
        relationValidator.stopNotOwnerOf(group);

        for (Integer memberId : memberIds) {
            Profile profile = profileProvider.getTarget(memberId);
            relator.relate(profile, group, RelationType.USUAL);
        }
    }

    public List<BriefProfileResponseDTO> getAllMyGroups() {
        Profile user = profileProvider.getUserFromAuth();
        return relationRepository.findAllByOwnerAndProfileType(user, ProfileType.GROUP).stream()
                .map(Relation::getProfile)
                .filter(Profile::isStatus)
                .map(profileMapper::mapToBrief)
                .collect(Collectors.toList());
    }

}
