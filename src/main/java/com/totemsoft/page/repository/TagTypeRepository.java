package com.totemsoft.page.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totemsoft.page.model.entity.TagType;

@Repository
public interface TagTypeRepository extends JpaRepository<TagType, Integer> {

    Optional<TagType> findByName(String name);

}
