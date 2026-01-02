package com.totemsoft.page.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totemsoft.page.model.entity.Page;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

}
