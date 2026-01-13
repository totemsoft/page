package com.totemsoft.page.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.totemsoft.page.model.entity.Key;
import com.totemsoft.page.model.entity.KeyTag;

@Repository
public interface KeyTagRepository extends JpaRepository<KeyTag, Long> {

    @Query("SELECT kt.key FROM KeyTag kt WHERE kt.tagId IN :tagIds")
    Set<Key> findByTagIdIn(
        @Param("tagIds") Set<Long> tagIds);

    @Query("SELECT kt.key FROM KeyTag kt WHERE kt.tag.tagTypeId = :tagTypeId AND UPPER(kt.tag.title) LIKE UPPER(:tagTitle)")
    Set<Key> findByTagTypeIdAndTagTitleContainingIgnoreCase(
        @Param("tagTypeId") Integer tagTypeId, @Param("tagTitle") String tagTitle);

}
