package com.totemsoft.page.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totemsoft.page.model.entity.ExchangeRate;
import com.totemsoft.page.model.entity.ExchangeRateId;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, ExchangeRateId> {

    boolean existsByDate(LocalDate date);

    List<ExchangeRate> findByDate(LocalDate date);

}
