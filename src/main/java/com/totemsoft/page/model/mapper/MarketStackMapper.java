package com.totemsoft.page.model.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.totemsoft.page.marketstack.v2.model.ExchangeDto;
import com.totemsoft.page.marketstack.v2.model.ExchangeTickerDto;
import com.totemsoft.page.model.entity.marketstack.Exchange;
import com.totemsoft.page.model.entity.marketstack.ExchangeTicker;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MarketStackMapper {

    List<ExchangeDto> mapExchange(Collection<Exchange> exchanges);
    Exchange mapExchange(ExchangeDto exchange);

    List<ExchangeTickerDto> mapExchangeTicker(Collection<ExchangeTicker> tickers);
    ExchangeTicker mapExchangeTicker(String mic, ExchangeTickerDto ticker);

}
