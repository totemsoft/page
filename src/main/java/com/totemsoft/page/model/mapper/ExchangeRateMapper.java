package com.totemsoft.page.model.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.totemsoft.page.exchangerates.v1.model.CurrencyDto;
import com.totemsoft.page.exchangerates.v1.model.ExchangeRateDto;
import com.totemsoft.page.model.entity.exchangerates.Currency;
import com.totemsoft.page.model.entity.exchangerates.ExchangeRate;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExchangeRateMapper {

    List<CurrencyDto> mapCurrency(Collection<Currency> currencies);

    ExchangeRateDto map(ExchangeRate exchangeRate);

}
