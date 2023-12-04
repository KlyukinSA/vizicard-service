package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vizicard.exception.CustomException;
import vizicard.model.*;
import vizicard.repository.CardRepository;
import vizicard.repository.CardTypeRepository;
import vizicard.repository.RelationRepository;
import vizicard.utils.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RelationService {

    private final RelationRepository relationRepository;
    private final CardRepository cardRepository;

    private final ProfileProvider profileProvider;
    private final RelationValidator relationValidator;

    private final ActionService actionService;
    private final EmailService emailService;

    private final EntityManager entityManager;
    private final Relator relator;
    private final CardService cardService;
    private final VcardFileService vcardFileService;
    private final CardTypeRepository cardTypeRepository;

    public void unrelate(Integer cardId) {
        Card card = cardRepository.findById(cardId).get();
        Account owner = profileProvider.getUserFromAuth();

        Relation relation = relationRepository.findByAccountOwnerAndCard(owner, card);
        if (relation == null || !relation.isStatus()) {
            throw new CustomException("No such relation", HttpStatus.CONFLICT);
        }

        relation.setStatus(false);
        relationRepository.save(relation);
    }

    public ResponseEntity<?> saveContact(Integer targetId) {
        Card target = profileProvider.getTarget(targetId);
        Account owner = profileProvider.getUserFromAuth();
        if (owner != null && !Objects.equals(target.getAccount().getId(), owner.getId())) {
            emailService.sendSaved(owner, target);

            relator.relate(owner, owner.getCurrentCard(), target, RelationType.SAVE);
            exchange(target, owner.getCurrentCard());
        }
        actionService.addSaveAction(owner, target);
        return getVcardResponse(target);
    }

    private ResponseEntity<?> getVcardResponse(Card target) {
        byte[] text = vcardFileService.getText(target);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/vcard"))
                .header("Content-Disposition", "attachment; filename=\""
                        + vcardFileService.getName(target) + '\"')
                .contentLength(text.length)
                .body(new InputStreamResource(new ByteArrayInputStream(text)));
    }

    public void leadGenerate(Integer targetId, Card leadCard, Card company, String email) {
        Card target = profileProvider.getTarget(targetId);

        if (company.getName() != null) {
            company.setType(cardTypeRepository.findByType(CardTypeEnum.COMPANY));
            company.setCustom(true);
            cardService.create(company);
        }
        leadCard.setType(cardTypeRepository.findByType(CardTypeEnum.PERSON));
        leadCard.setCustom(true);
        leadCard.setAccount(target.getAccount());
        cardService.create(leadCard);
        relationRepository.save(new Relation(target.getAccount(), leadCard, company, RelationType.EMPLOYEE));

        emailService.sendSaved(email, target);
        emailService.sendLead(target.getAccount(), leadCard);

        exchange(target, leadCard);
    }

    private void exchange(Card target, Card actor) {
        relator.relate(target.getAccount(), target, actor, RelationType.EXCHANGE);
    }

    public List<Relation> searchLike(String name, String type) {
        Account user = profileProvider.getUserFromAuth();
        StringBuilder query = new StringBuilder( // should search as in Relator::relate
                "select relation.id from relation inner join card on relation.card_id=card.id where account_owner_id=")
                .append(user.getId());

        if (name != null) {
            for (String part : name.split(" ")) {
                query.append(" and card.name like '").append(surround(part)).append("'");
            }
        }

        if (type != null) {
            query.append(" and card.type like '").append(surround(type)).append("'");
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
                .filter((relation) -> relation.getCard().isStatus())
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
        Account user = profileProvider.getUserFromAuth();
        List<Relation> level1s = relationRepository.findAllByTypeAndAccountOwner(RelationType.REFERRER, user);
        List<Relation> level2s = relationRepository.findAllByTypeAndAccountOwner(RelationType.REFERRER_LEVEL2, user);
        if (level == null) {
            level1s.addAll(level2s);
            return  level1s;
        } else if (level == 1) {
            return level1s;
        } else {
            return level2s;
        }
    }

    public Card createRelationCard(Card card) {
        card.setCustom(true);
        cardService.createMyCard(card);
        Account account = profileProvider.getUserFromAuth();
        relator.relate(account, account.getCurrentCard(), card, RelationType.OWNER);
        return card;
    }

    public Stream<Relation> getRelationsByAuth() {
        return relationRepository.findAllByAccountOwner(profileProvider.getUserFromAuth()).stream()
                .filter(Relation::isStatus);
    }

}
