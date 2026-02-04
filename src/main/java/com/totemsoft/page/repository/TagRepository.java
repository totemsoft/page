package com.totemsoft.page.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totemsoft.page.model.entity.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByTagTypeId(int tagTypeId);

    List<Tag> findByTagTypeIdAndNameContainingIgnoreCase(int tagTypeId, String name);

}
