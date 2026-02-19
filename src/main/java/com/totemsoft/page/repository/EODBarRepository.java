package com.totemsoft.page.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totemsoft.page.model.entity.marketstack.EODBar;
import com.totemsoft.page.model.entity.marketstack.EODBarId;

@Repository
public interface EODBarRepository extends JpaRepository<EODBar, EODBarId> {

    boolean existsByExchangeAndDateAfter(String exchange, Instant date);

    List<EODBar> findByExchangeAndDateAfter(String exchange, Instant date);

}
