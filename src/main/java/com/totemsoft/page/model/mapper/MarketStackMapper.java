package com.totemsoft.page.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.totemsoft.page.marketstack.v2.model.ExchangeDto;
import com.totemsoft.page.model.entity.marketstack.Exchange;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MarketStackMapper {

    //List<ExchangeDto> map(Collection<Exchange> exchanges);
    Exchange map(ExchangeDto exchange);

}
