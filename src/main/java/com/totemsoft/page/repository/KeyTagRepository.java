package com.totemsoft.page.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.totemsoft.page.model.entity.KeyTag;
import com.totemsoft.page.model.entity.KeyTagId;

@Repository
public interface KeyTagRepository extends JpaRepository<KeyTag, KeyTagId>, JpaSpecificationExecutor<KeyTag> {

    @Query("FROM KeyTag kt WHERE kt.tagId IN :tagIds")
    Set<KeyTag> findByTagIdIn(
        @Param("tagIds") Set<Long> tagIds);

    @Query("FROM KeyTag kt WHERE kt.tag.tagTypeId = :tagTypeId AND UPPER(kt.tag.title) LIKE UPPER(:tagTitle)")
    Set<KeyTag> findByTagTypeIdAndTagTitleContainingIgnoreCase(
        @Param("tagTypeId") Integer tagTypeId,
        @Param("tagTitle") String tagTitle);

    void deleteAllByKeyId(Long keyId);
}
