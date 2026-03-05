package com.totemsoft.page.model.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.totemsoft.page.exchangerates.v1.model.ExchangeRateDto;
import com.totemsoft.page.model.Cell;
import com.totemsoft.page.model.SeriesDataDto;
import com.totemsoft.page.model.entity.SeriesData;
import com.totemsoft.page.model.entity.exchangerates.Currency;

import lombok.extern.log4j.Log4j2;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@Log4j2
public abstract class SeriesDataMapper {

    public abstract SeriesDataDto map(SeriesData data);
    public abstract Cell<BigDecimal> mapSeriesData(SeriesData data);

    public Cell<BigDecimal> mapSeriesData(SeriesData sd, ExchangeRateDto exchangeRate) {
        final var cell = this.mapSeriesData(sd);
        cell.setValue(convert(sd, exchangeRate));
        return cell;
    }

    public List<SeriesDataDto> mapSeriesData(Collection<SeriesData> data, ExchangeRateDto exchangeRate) {
        final var result = new ArrayList<SeriesDataDto>(data.size());
        data.forEach(sd -> {
            final var sdt = this.map(sd);
            sdt.setValue(convert(sd, exchangeRate));
            result.add(sdt);
        });
        return result;
    }

    private BigDecimal convert(SeriesData sd, ExchangeRateDto er) {
        final var sdt = this.map(sd);
        final var value = sdt.getValue();
        final var rate = er == null ? null : er.getRate();
        if (rate == null || rate.signum() == 0) {
            log.warn("Zero rate: {}", er);
            //return value;
        }
        // Special case: exchangeRate(s) EUR/EUR, EUR/USD, etc
        if (sd.getKey().findTag(Currency.CURRENCY_BASE).isPresent()) {
            return divide(value, rate);
        }
        // General case: seriesData EUR/EUR or USD/USD
        if (er.getCode().equals(sdt.getCurrency())) {
            // eg sd:USD/USD vs er:EUR/AUD
            return divide(value, rate);
        }
        // eg sd:USD/USD stock(s) or sd:EUR/EUR generated data
        return multiply(value, rate);
    }

    private BigDecimal divide(BigDecimal value, BigDecimal rate) {
        return value.divide(rate, value.scale() + rate.scale(), RoundingMode.HALF_UP);
    }

    private BigDecimal multiply(BigDecimal value, BigDecimal rate) {
        return value.multiply(rate).setScale(value.scale() + rate.scale(), RoundingMode.HALF_UP);
    }

}
