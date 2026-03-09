package com.totemsoft.page.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.totemsoft.page.marketstack.v2.api.MarketStackApi;
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
@Transactional
@RequiredArgsConstructor
@Log4j2
class MarketStackService {

    private final MarketStackApi marketStackApi;

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

    void saveExchangeTickers(String mic, int limit, int remainder) {
        log.debug(">>> saving exchangeTickers for: {}", mic);
        final var total = exchangeTickerRepository.countByMic(mic);
        // XNAS total=45270 (NASDAQ - ALL MARKETS)
        if (remainder >= 0 && total % limit != remainder) {
            log.debug("<<< {} exchangeTickers already loaded for: {}", total, mic);
            return;
        }
        try {
            final var response = marketStackApi.exchangeTickers(mic,
                Optional.of(limit), Optional.of(total));
            log.debug(">>> exchangeTickers found: {} {}", mic, response.getPagination());
            final var tickers = response.getData().getTickers();
            tickers.forEach(ticker -> exchangeTickerRepository.save(marketStackMapper.mapExchangeTicker(mic, ticker)));
        } catch (ApiException ignore) {
            // marketStackApi error will be logged in RestClient.defaultStatusHandler
        }
    }

    List<ExchangeTicker> findExchangeTickersBase(String mic) {
        return exchangeTickerRepository.findByMicAndBaseTrue(mic);
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

    // api/supported_tickers_2.csv
    // ticker,exchange,assetType,priceCurrency,startDate,endDate
    // AAPL,NASDAQ,Stock,USD,1980-12-12,2025-03-12
    Tag saveExchangeTickersEODTag(EODBar entity) {
        final var assetType = entity.getAssetType() != null ? entity.getAssetType() : EODBar.ASSET_CLASS_STOCK;
        log.debug("tagging as {}: {}", assetType, entity);
        return keyTaggingService.saveTag(EODBar.ASSET_CLASS, assetType, entity.getName());
    }

}
