package com.totemsoft.page.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totemsoft.page.model.entity.marketstack.EODBar;
import com.totemsoft.page.model.entity.marketstack.EODBarId;

@Repository
public interface EODBarRepository extends JpaRepository<EODBar, EODBarId> {

    /** where exchange = :exchange and date = :date */
    boolean existsByExchangeAndDate(String exchange, Instant date);
    /** where exchange = :exchange and date > :date */
    boolean existsByExchangeAndDateAfter(String exchange, Instant date);
    /** where exchange = :exchange and date between :date1 and :date2 (inclusive) */
    boolean existsByExchangeAndDateBetween(String exchange, Instant date1, Instant date2);

    List<EODBar> findByExchangeAndDate(String exchange, Instant date);
    List<EODBar> findByExchangeAndDateAfter(String exchange, Instant date);

}
