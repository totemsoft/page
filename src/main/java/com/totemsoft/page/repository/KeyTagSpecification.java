package com.totemsoft.page.repository;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import com.totemsoft.page.model.entity.KeyTag;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KeyTagSpecification implements Specification<KeyTag> {

    private static final long serialVersionUID = 1L;

    private final Set<Long> tagIds;
    private final Map<Integer, String> tagTitles;

    @Override
    public @Nullable Predicate toPredicate(Root<KeyTag> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        final var predicates = new ArrayList<Predicate>();
        if (!tagIds.isEmpty()) {
            predicates.add(
                root.get("tagId").in(tagIds)
            );
        }
        if (!tagTitles.isEmpty()) {
            final var tag = root.join("tag");
            tagTitles.forEach((tagTypeId, tagTitle) -> {
                predicates.add(
                    criteriaBuilder.equal(tag.get("tagTypeId"), tagTypeId)
                );
                final var likePattern = '%' + tagTitle.trim().toLowerCase() + '%';
                predicates.add(
                    criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(tag.get("name")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(tag.get("title")), likePattern)
                    )
                );
            });
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }

}
