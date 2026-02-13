package com.totemsoft.page.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totemsoft.page.model.entity.Currency;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, String> {

    List<Currency> findByBaseTrue();

}
