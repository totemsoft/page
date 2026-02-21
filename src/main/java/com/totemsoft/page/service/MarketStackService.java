package com.totemsoft.page.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.totemsoft.page.marketstack.v2.model.EODBarDto;
import com.totemsoft.page.marketstack.v2.model.ExchangeDto;
import com.totemsoft.page.marketstack.v2.model.ExchangeTickerDto;
import com.totemsoft.page.model.entity.Tag;
import com.totemsoft.page.model.entity.marketstack.EODBar;
import com.totemsoft.page.model.entity.marketstack.Exchange;
import com.totemsoft.page.model.entity.marketstack.ExchangeTicker;
import com.totemsoft.page.model.mapper.MarketStackMapper;
import com.totemsoft.page.repository.EODBarRepository;
import com.totemsoft.page.repository.ExchangeRepository;
import com.totemsoft.page.repository.ExchangeTickerRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
class MarketStackService {

    private final KeyTaggingService keyTaggingService;

    private final EODBarRepository eodBarRepository;
    private final ExchangeRepository exchangeRepository;
    private final ExchangeTickerRepository exchangeTickerRepository;

    private final MarketStackMapper marketStackMapper;

    int countExchanges() {
        return (int) exchangeRepository.count();
    }

    List<String> findExchangeBaseMic() {
        return exchangeRepository.findByBaseTrue()
            .stream().map(e -> e.getMic()).toList();
    }

    void saveExchanges(List<ExchangeDto> exchanges) {
        exchanges.forEach(this::saveExchange);
    }

    Exchange saveExchange(ExchangeDto dto) {
        return exchangeRepository.save(marketStackMapper.mapExchange(dto));
    }

    int countExchangeTickers(String mic) {
        return exchangeTickerRepository.countByMic(mic);
    }

    List<ExchangeTicker> findExchangeTickersBase(String mic) {
        return exchangeTickerRepository.findByMicAndBaseTrue(mic);
    }

    void saveExchangeTickers(String mic, List<ExchangeTickerDto> tickers) {
        tickers.forEach(ticker -> exchangeTickerRepository.save(marketStackMapper.mapExchangeTicker(mic, ticker)));
    }

    ExchangeTicker saveExchangeTicker(String mic, ExchangeTickerDto dto) {
        return exchangeTickerRepository.save(marketStackMapper.mapExchangeTicker(mic, dto));
    }

    void saveExchangeTickersEOD(List<EODBarDto> bars) {
        bars.forEach(this::saveExchangeTickerEOD);
    }

    EODBar saveExchangeTickerEOD(EODBarDto dto) {
        final var entity = eodBarRepository.save(marketStackMapper.mapEODBar(dto));
        keyTaggingService.saveSeriesDataKey(entity);
        return entity;
    }

    void saveExchangeTickersEODTags(String exchange, Instant date) {
        final var bars = eodBarRepository.findByExchangeAndDateAfter(exchange, date);
        bars.forEach(this::saveExchangeTickersEODTag);
    }

    Tag saveExchangeTickersEODTag(EODBar entity) {
        log.debug("tagging: {}", entity);
        if (entity.getAssetType() != null) {
            return keyTaggingService.saveTag(EODBar.ASSET_CLASS, entity.getAssetType(), entity.getName());
        }
        return null;
    }

}
