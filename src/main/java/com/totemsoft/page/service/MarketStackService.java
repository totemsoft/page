package com.totemsoft.page.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.totemsoft.page.marketstack.v2.model.ExchangeDto;
import com.totemsoft.page.marketstack.v2.model.ExchangeTickerDto;
import com.totemsoft.page.model.mapper.MarketStackMapper;
import com.totemsoft.page.repository.ExchangeRepository;
import com.totemsoft.page.repository.ExchangeTickerRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarketStackService {

    private final ExchangeRepository exchangeRepository;

    private final ExchangeTickerRepository exchangeTickerRepository;

    private final MarketStackMapper marketStackMapper;

    @Transactional
    int countExchanges() {
        return (int) exchangeRepository.count();
    }

    @Transactional
    List<String> findExchangeBaseMic() {
        return exchangeRepository.findByBaseTrue()
            .stream().map(e -> e.getMic()).toList();
    }

    @Transactional
    void saveExchanges(List<ExchangeDto> exchanges) {
        exchanges.forEach(exchange -> exchangeRepository.save(marketStackMapper.mapExchange(exchange)));
    }

    @Transactional
    int countExchangeTickers(String mic) {
        return (int) exchangeTickerRepository.countByMic(mic);
    }

    @Transactional
    void saveExchangeTickers(String mic, List<ExchangeTickerDto> tickers) {
        tickers.forEach(ticker -> exchangeTickerRepository.save(marketStackMapper.mapExchangeTicker(mic, ticker)));
    }

}
