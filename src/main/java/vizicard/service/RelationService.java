package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vizicard.dto.LeadGenerationDTO;
import vizicard.dto.ProfileCreateDTO;
import vizicard.dto.RelationResponseDTO;
import vizicard.exception.CustomException;
import vizicard.model.Profile;
import vizicard.model.Relation;
import vizicard.model.RelationType;
import vizicard.repository.RelationRepository;
import vizicard.utils.ProfileMapper;
import vizicard.utils.ProfileProvider;
import vizicard.utils.RelationValidator;
import vizicard.utils.VcardFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelationService {

    private final RelationRepository relationRepository;

    private final ProfileProvider profileProvider;
    private final RelationValidator relationValidator;

    private final ProfileMapper profileMapper;

    private final ActionService actionService;
    private final EmailService emailService;
    private final ProfileService profileService;

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

    public List<RelationResponseDTO> getRelations() {
        Profile user = profileProvider.getUserFromAuth();
        return relationRepository.findAllByOwnerOrderByProfileNameAsc(user).stream()
                .filter(Relation::isStatus)
                .filter((val) -> !Objects.equals(val.getProfile().getId(), user.getId()))
                .filter((val) -> val.getProfile().isStatus())
                .map((val) -> new RelationResponseDTO(profileMapper.mapBrief(val.getProfile()), val.getCreateAt()))
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> relate(Integer targetProfileId) throws Exception {
        Profile target = profileProvider.getTarget(targetProfileId);

        Profile owner = profileProvider.getUserFromAuth();
        if (owner != null && !Objects.equals(target.getId(), owner.getId())) {
            try {
                emailService.sendRelation(target.getUsername(), owner.getName(), owner.getId());
            } catch (Exception e) {
                System.out.println("tried to send message from " + owner.getId() + " to " + target.getId() + "\nbut\n");
                e.printStackTrace();
            }

            Relation relation = relationRepository.findByOwnerAndProfile(owner, target);
            if (relation == null || !relation.isStatus()) {
                relationRepository.save(new Relation(owner, target, RelationType.USUAL));
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

        Profile author = profileProvider.getUserFromAuth();
        if (author != null) {
            if (Objects.equals(target.getId(), author.getId())) return;
            System.out.println("before find \n");
            Relation relation = relationRepository.findByOwnerAndProfile(target, author);
            System.out.println("before save \n");
            if (relation == null || !relation.isStatus()) {
                relationRepository.save(new Relation(target, author, RelationType.OWNER));
            }
        } else {
            profileService.createProfile(dto, target, null);
        }

        try {
            emailService.sendUsual(target.getUsername(), "Вам прислали новый контакт в ViziCard", getLeadGenMessage(dto, author));
        } catch (Exception ignored) {}
    }

    private String getLeadGenMessage(ProfileCreateDTO dto, Profile author) {
//        String res = dto.toString();
//        if (author != null) {
//            res += "\n\n" + author;
//        }
        return null;
    }

}
