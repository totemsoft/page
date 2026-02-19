package com.totemsoft.page.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totemsoft.page.model.entity.marketstack.ExchangeTicker;
import com.totemsoft.page.model.entity.marketstack.ExchangeTickerId;

@Repository
public interface ExchangeTickerRepository extends JpaRepository<ExchangeTicker, ExchangeTickerId> {

    int countByMic(String mic);

    List<ExchangeTicker> findByMic(String mic, Pageable pageable);

    List<ExchangeTicker> findByMicAndBaseTrue(String mic);

}
