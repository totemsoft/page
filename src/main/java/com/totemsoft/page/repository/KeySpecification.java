package com.totemsoft.page.repository;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import com.totemsoft.page.model.entity.Key;
import com.totemsoft.page.model.entity.Tag;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KeySpecification implements Specification<Key> {

    private static final long serialVersionUID = 1L;

    private final Set<Long> tagIds;
    private final Map<Integer, String> tagTitles;

    @Override
    public @Nullable Predicate toPredicate(Root<Key> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        //query.distinct(true);
        final var predicates = new ArrayList<Predicate>();
        if (!tagIds.isEmpty()) {
            tagIds.forEach(tagId -> predicates.add(
                criteriaBuilder.isMember(Tag.builder().id(tagId).build(), root.get("tags"))
            ));
        }
        if (!tagTitles.isEmpty()) {
            final var tags = root.get("tags");
            tagTitles.forEach((tagTypeId, tagTitle) -> {
                predicates.add(
                    criteriaBuilder.equal(tags.get("tagTypeId"), tagTypeId)
                );
                final var likePattern = '%' + tagTitle.trim().toLowerCase() + '%';
                predicates.add(
                    criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(tags.get("name")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(tags.get("title")), likePattern)
                    )
                );
            });
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }

}
