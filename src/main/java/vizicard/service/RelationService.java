package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vizicard.dto.profile.LeadGenDTO;
import vizicard.dto.profile.ProfileCreateDTO;
import vizicard.exception.CustomException;
import vizicard.model.Profile;
import vizicard.model.ProfileType;
import vizicard.model.Relation;
import vizicard.model.RelationType;
import vizicard.repository.ProfileRepository;
import vizicard.repository.RelationRepository;
import vizicard.utils.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelationService {

    private final RelationRepository relationRepository;
    private final ProfileRepository profileRepository;

    private final ProfileProvider profileProvider;
    private final RelationValidator relationValidator;
    private final ModelMapper modelMapper;

    private final ActionService actionService;
    private final EmailService emailService;
    private final ProfileService profileService;

    private final EntityManager entityManager;
    private final Relator relator;

    public void unrelate(Integer ownerId, Integer profileId) {
        Profile owner;
        if (ownerId == null) {
            owner = profileProvider.getUserFromAuth();
        } else {
            owner = profileProvider.getTarget(ownerId);
            relationValidator.stopNotOwnerOf(owner);
        }
        Profile target = profileProvider.getTarget(profileId);

        Relation relation = relationRepository.findByOwnerAndProfile(owner, target);
        if (relation == null || !relation.isStatus()) {
            throw new CustomException("No such relation", HttpStatus.CONFLICT);
        }

        relation.setStatus(false);
        relationRepository.save(relation);
    }

    public ResponseEntity<?> saveContact(Integer targetProfileId) throws Exception {
        Profile target = profileProvider.getTarget(targetProfileId);

        Profile owner = profileProvider.getUserFromAuth();
        if (owner != null && !Objects.equals(target.getId(), owner.getId())) {
            emailService.sendSaved(owner, target);

            relator.relate(owner, target, RelationType.USUAL);

            if (target.getCompany() != null && target.getCompany().isStatus()) {
                relator.relate(owner, target.getCompany(), RelationType.USUAL);
            }
        }

        actionService.addSaveAction(owner, target);

        return getVcardResponse(new VcardFile(target));
    }

    private ResponseEntity<?> getVcardResponse(VcardFile vcardFile) {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/vcard"))
                .header("Content-Disposition", "attachment; filename=\"" + vcardFile.getName() + '\"')
                .contentLength(vcardFile.getBytes().length)
                .body(new InputStreamResource(new ByteArrayInputStream(vcardFile.getBytes())));
    }

    public void leadGenerate(Integer targetProfileId, LeadGenDTO dto) {
        Profile target = profileProvider.getTarget(targetProfileId);
        Profile company = null;
        RelationType relationType;

        Profile author = profileProvider.getUserFromAuth();
        if (author != null) {
            if (Objects.equals(target.getId(), author.getId())) {
                return;
            }
            company = author.getCompany();
            relationType = RelationType.USUAL;
        } else {
            ProfileCreateDTO dto1 = modelMapper.map(dto, ProfileCreateDTO.class);
            dto1.setType(ProfileType.LEAD_USER);
            author = profileService.createProfile(dto1, target, null, null, RelationType.OWNER);
            if (dto.getCompanyName() != null) {
                ProfileCreateDTO dto2 = new ProfileCreateDTO();
                dto2.setName(dto.getCompanyName());
                dto2.setType(ProfileType.LEAD_COMPANY);
                company = profileService.createProfile(dto2, author, null, null, RelationType.OWNER);
                author.setCompany(company);
                profileRepository.save(author);
            }
            // guest `author` maybe gave his email in LeadGenDTO. now we can send `target` to him
            emailService.sendSaved(author, target);
            relationType = RelationType.OWNER;
        }

        relator.relate(target, author, relationType);

        if (company != null && company.isStatus()) {
            relator.relate(target, company, RelationType.USUAL);
        }

        emailService.sendLead(target, author);
    }

    public List<Relation> searchLike(String name, String type) {
        Profile user = profileProvider.getUserFromAuth();
        StringBuilder query = new StringBuilder(
                "select relation.id from relation inner join profile on relation.profile_id=profile.id where owner_id=")
                .append(user.getId());

        if (name != null) {
            for (String part : name.split(" ")) {
                query.append(" and profile.name like '").append(surround(part)).append("'");
            }
        }

        if (type != null) {
            query.append(" and profile.type like '").append(surround(type)).append("'");
        }

        query.append(" order by relation.id desc");

        Query nativeQuery = entityManager.createNativeQuery(query.toString());

        List<Integer> ids;
        try {
            ids = nativeQuery.getResultList();
        } catch (Exception e) {
            throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        return ids.stream()
                .map((id) -> relationRepository.findById(id).get())
                .filter(Relation::isStatus)
                .filter((relation) -> relation.getProfile().isStatus())
                .filter(relation -> relation.getType() != RelationType.SECONDARY)
                .collect(Collectors.toList());
    }

    private String surround(String s) {
        if (!s.startsWith("%")) {
            s = "%" + s;
        }
        if (!s.endsWith("%")) {
            s = s + "%";
        }
        return s;
    }

    public List<Relation> getReferralsWithLevelOrAll(Integer level) {
        Profile user = profileProvider.getUserFromAuth();
        List<Relation> level1s = relationRepository.findAllByTypeAndOwner(RelationType.REFERRER, user);
        List<Relation> level2s = relationRepository.findAllByTypeAndOwner(RelationType.REFERRER_LEVEL2, user);
        if (level == null) {
            level1s.addAll(level2s);
            return  level1s;
        } else if (level == 1) {
            return level1s;
        } else {
            return level2s;
        }
    }

}
