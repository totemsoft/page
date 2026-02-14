package com.totemsoft.page.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totemsoft.page.model.entity.exchangerates.ExchangeRate;
import com.totemsoft.page.model.entity.exchangerates.ExchangeRateId;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, ExchangeRateId> {

    boolean existsByDate(LocalDate date);

    List<ExchangeRate> findByDate(LocalDate date);

}
