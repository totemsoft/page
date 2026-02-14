package com.totemsoft.page.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.totemsoft.page.marketstack.v2.model.ExchangeDto;
import com.totemsoft.page.model.mapper.MarketStackMapper;
import com.totemsoft.page.repository.ExchangeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarketStackService {

    private final ExchangeRepository exchangeRepository;

    private final MarketStackMapper marketStackMapper;

    @Transactional
    int countExchanges() {
        return (int) exchangeRepository.count();
    }

    @Transactional
    void saveExchanges(List<ExchangeDto> exchanges) {
        exchanges.forEach(exchange -> exchangeRepository.save(marketStackMapper.map(exchange)));
    }

}
