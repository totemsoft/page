package com.totemsoft.page.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.totemsoft.page.exchangerates.v1.model.ExchangeRateDto;
import com.totemsoft.page.model.entity.exchangerates.ExchangeRate;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExchangeRateMapper {

    ExchangeRateDto map(ExchangeRate exchangeRate);

}
