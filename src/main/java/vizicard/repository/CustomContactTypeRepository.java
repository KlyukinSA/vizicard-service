package vizicard.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import vizicard.exception.CustomException;
import vizicard.model.Account;
import vizicard.model.ContactEnum;
import vizicard.model.ContactGroupEnum;
import vizicard.model.ContactType;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CustomContactTypeRepository {
    private final EntityManager entityManager;
    private final ContactTypeRepository contactTypeRepository;
    public List<ContactType> findAllByLikeContactTypeOrGroupTypeOrTheirWriting(String contactType, String groupType, String theirWriting) {
//        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//        CriteriaQuery<ContactType> cq = cb.createQuery(ContactType.class);
//        Root<ContactType> root = cq.from(ContactType.class);
//        List<Predicate> predicates = new ArrayList<>();
//        if (contactType != null) {
//            predicates.add(cb.like(root.get("type"), "%" +  contactType + "%")); // runtime: cant compare enum and string
//        }
//        if (groupType != null) {
//            predicates.add(cb.like(root.get("group.type"), "%" + groupType + "%"));
//        }
//        if (theirWriting != null) {
//            predicates.add(cb.like(root.get("writing"), "%" + theirWriting + "%"));
//            predicates.add(cb.like(root.get("group.writing"), "%" + theirWriting + "%"));
//        }
//        cq.where(predicates.toArray(new Predicate[0]));
//        return entityManager.createQuery(cq).getResultList();
        StringBuilder query = new StringBuilder(
                "select contact_type.id from contact_type inner join contact_group on contact_group.id=contact_type.group_id");

        if (contactType != null) {
            query.append(" and contact_type.type like '").append(surround(contactType)).append("'");
        }
        if (groupType != null) {
            query.append(" and contact_group.type like '").append(surround(groupType)).append("'");
        }
        if (theirWriting != null) {
            query.append(" and (contact_type.writing like '").append(surround(theirWriting)).append("'");
            query.append(" or contact_group.writing like '").append(surround(theirWriting)).append("')");
        }

        Query nativeQuery = entityManager.createNativeQuery(query.toString());

        List<Integer> ids;
        try {
            ids = nativeQuery.getResultList();
        } catch (Exception e) {
            throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return ids.stream()
                .map(contactTypeRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
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
