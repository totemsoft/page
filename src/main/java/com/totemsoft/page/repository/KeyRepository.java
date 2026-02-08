package com.totemsoft.page.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.totemsoft.page.model.entity.Key;
import com.totemsoft.page.model.entity.Tag;

@Repository
public interface KeyRepository extends JpaRepository<Key, Long> {

    @Query("SELECT k FROM Key k WHERE :tag MEMBER OF k.tags")
    Set<Key> findByTag(@Param("tag") Tag tag);

    Optional<Key> findByName(String name);

}
