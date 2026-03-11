package com.totemsoft.page.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.totemsoft.page.marketstack.v2.api.MarketStackApi;
import com.totemsoft.page.marketstack.v2.model.EODBarDto;
import com.totemsoft.page.marketstack.v2.model.ExchangeDto;
import com.totemsoft.page.marketstack.v2.model.ExchangeTickerDto;
import com.totemsoft.page.marketstack.v2.model.PaginationDto;
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

    static final int LIMIT = 1000;

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

    void saveExchanges() {
        // retrieve exchanges via API
        final var total = this.countExchanges(); // pagination.total=2817
        if (total < 2817) {
            final var response = marketStackApi.exchanges(
                Optional.of(LIMIT), Optional.of(total), Optional.empty());
            log.debug(">>> exchanges found: {}", response.getPagination());
            final var exchanges = response.getData();
            exchanges.forEach(this::saveExchange);
        } else {
            log.debug("<<< marketStackTask exchanges already loaded");
        }
    }

    private Exchange saveExchange(ExchangeDto dto) {
        return exchangeRepository.save(marketStackMapper.mapExchange(dto));
    }

    PaginationDto saveExchangeTickers(String mic, int limit, int remainder) {
        if (StringUtils.isBlank(mic)) {
            return null;
        }
        final var total = exchangeTickerRepository.countByMic(mic);
        // e.g. XNAS (NASDAQ - ALL MARKETS) total=45282
        if (remainder >= 0 && total % limit != remainder) {
            log.debug("<<< {} exchangeTickers already loaded for: {}", total, mic);
            return PaginationDto.builder()
                .limit(limit)
                .total(total)
                .build();
        }
        try {
            final var response = marketStackApi.exchangeTickers(mic,
                Optional.of(limit), Optional.of(total));
            final var pagination = response.getPagination();
            log.debug(">>> exchangeTickers found: {} {}", mic, pagination);
            final var tickers = response.getData().getTickers();
            tickers.forEach(ticker -> exchangeTickerRepository.save(marketStackMapper.mapExchangeTicker(mic, ticker)));
            return pagination;
        } catch (ApiException ignore) {
            // marketStackApi error will be logged in RestClient.defaultStatusHandler
            return null;
        }
    }

    ExchangeTicker saveExchangeTicker(String mic, ExchangeTickerDto dto) {
        return exchangeTickerRepository.save(marketStackMapper.mapExchangeTicker(mic, dto));
    }

    void saveExchangeTickersEOD(String mic, LocalDate date) {
        final var instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        final var total = 0; // marketStackService.countExchangeTickersEOD(mic, instant);
        if (eodBarRepository.existsByExchangeAndDateAfter(mic, instant)) {
            log.info("<<< eodBar(s) already loaded for: {}, {}", mic, date);
            return;
        }
        final var tickers = findExchangeTickersBase(mic);
        if (tickers.isEmpty()) {
            log.info("<<< no tickers found for: {}", mic);
            return;
        }
        final var symbols = tickers.stream().map(ExchangeTicker::getSymbol).toList();
        try {
            final var response = marketStackApi.eodDate(date, Optional.of(mic), symbols,
                Optional.of(LIMIT), Optional.of(total), Optional.empty());
            final var pagination = response.getPagination();
            log.debug(">>> eodBars found: {}, {}, {}, {}", mic, date, symbols, pagination);
            if (pagination.getCount() > 0) {
                final var bars = response.getData();
                bars.forEach(this::saveExchangeTickerEOD);
                saveExchangeTickersEODTags(mic, instant);
            }
        } catch (ApiException ignore) {
            // marketStackApi error will be logged in RestClient.defaultStatusHandler
        }
    }

    private List<ExchangeTicker> findExchangeTickersBase(String mic) {
        return exchangeTickerRepository.findByMicAndBaseTrue(mic);
    }

    private EODBar saveExchangeTickerEOD(EODBarDto dto) {
        final var entity = eodBarRepository.save(marketStackMapper.mapEODBar(dto));
        keyTaggingService.saveSeriesDataKey(entity);
        return entity;
    }

    private void saveExchangeTickersEODTags(String exchange, Instant date) {
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
