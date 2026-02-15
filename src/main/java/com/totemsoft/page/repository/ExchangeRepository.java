package com.totemsoft.page.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totemsoft.page.model.entity.marketstack.Exchange;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, String> {

    List<Exchange> findByBaseTrue();

}
