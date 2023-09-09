package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vizicard.dto.ProfileCreateDTO;
import vizicard.dto.RelationResponseDTO;
import vizicard.exception.CustomException;
import vizicard.model.Profile;
import vizicard.model.Relation;
import vizicard.model.RelationType;
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

    private final ProfileProvider profileProvider;
    private final RelationValidator relationValidator;
    private final ProfileCompanySetter profileCompanySetter;

    private final ActionService actionService;
    private final EmailService emailService;
    private final ProfileService profileService;

    private final EntityManager entityManager;

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

    public ResponseEntity<?> relate(Integer targetProfileId) throws Exception {
        Profile target = profileProvider.getTarget(targetProfileId);

        Profile owner = profileProvider.getUserFromAuth();
        if (owner != null && !Objects.equals(target.getId(), owner.getId())) {
            try {
                emailService.sendRelation(target, owner);
            } catch (Exception e) {
                System.out.println("tried to send message from " + owner.getId() + " to " + target.getId() + "\nbut\n");
                e.printStackTrace();
            }

            Relation relation = relationRepository.findByOwnerAndProfile(owner, target);
            if (relation == null || !relation.isStatus()) {
                relationRepository.save(new Relation(owner, target, RelationType.USUAL));
            }

            if (target.getCompany() != null && target.getCompany().isStatus()) {
                relationRepository.save(new Relation(owner, target.getCompany(), RelationType.USUAL));
            }
        }

        actionService.save(owner, target);

        return getVcardResponse(new VcardFile(target));
    }

    private ResponseEntity<?> getVcardResponse(VcardFile vcardFile) {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/vcard"))
                .header("Content-Disposition", "attachment; filename=\"" + vcardFile.getName() + '\"')
                .contentLength(vcardFile.getBytes().length)
                .body(new InputStreamResource(new ByteArrayInputStream(vcardFile.getBytes())));
    }

    public void leadGenerate(Integer targetProfileId, ProfileCreateDTO dto) {
        Profile target = profileProvider.getTarget(targetProfileId);
        Profile company = null;

        Profile author = profileProvider.getUserFromAuth();
        if (author != null) {
            if (Objects.equals(target.getId(), author.getId())) return;
            company = author.getCompany();
        } else {
            author = profileService.createProfile(dto, target, null);
            if (dto.getCompanyId() != null) {
                company = profileProvider.getTarget(dto.getCompanyId());
            }
            
            try {
                emailService.sendSaved(author, target);
            } catch (Exception ignored) {}
        }

        Relation relation = relationRepository.findByOwnerAndProfile(target, author);
        if (relation == null || !relation.isStatus()) {
            relationRepository.save(new Relation(target, author, RelationType.OWNER));
        }
        if (company != null) {
            profileCompanySetter.addRelation(target, company);
        }

        try {
            emailService.sendLead(target, author);
        } catch (Exception ignored) {}
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

}
