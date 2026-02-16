package com.totemsoft.page.model.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.totemsoft.page.model.Cell;
import com.totemsoft.page.model.SeriesDataDto;
import com.totemsoft.page.model.entity.SeriesData;
import com.totemsoft.page.model.entity.exchangerates.ExchangeRate;

import lombok.extern.log4j.Log4j2;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@Log4j2
public abstract class SeriesDataMapper {

    public abstract List<SeriesDataDto> mapSeriesData(Collection<SeriesData> data);
    public abstract Cell<BigDecimal> mapSeriesData(SeriesData data);

    public Cell<BigDecimal> mapSeriesData(SeriesData sd, Optional<ExchangeRate> exchangeRate) {
        final var cell = this.mapSeriesData(sd);
        exchangeRate.ifPresent(er -> cell.setValue(convert(sd, er)));
        return cell;
    }

    public List<SeriesDataDto> mapSeriesData(Collection<SeriesData> data, Optional<ExchangeRate> exchangeRate) {
        final var result = this.mapSeriesData(data);
        exchangeRate.ifPresent(er -> result.forEach(sd -> sd.setValue(convert(sd, er))));
        return result;
    }

    private BigDecimal convert(SeriesData sd, ExchangeRate exchangeRate) {
        // TODO: SeriesData baseCurrency/currency VS ExchangeRate base/code
        final var value = sd.getValue();
        final var rate = exchangeRate.getRate();
        if (rate == null || rate.signum() == 0) {
            log.warn("Zero rate: {}", exchangeRate);
            //return value;
        }
        if (sd.sameCurrency()) {
            return multiply(value, rate);
        } else {
            return divide(value, rate);
        }
    }

    private BigDecimal convert(SeriesDataDto sd, ExchangeRate exchangeRate) {
        // TODO: SeriesData baseCurrency/currency VS ExchangeRate base/code
        final var value = sd.getValue();
        final var rate = exchangeRate.getRate();
        if (rate == null || rate.signum() == 0) {
            log.warn("Zero rate: {}", exchangeRate);
            //return value;
        }
        if (sd.sameCurrency()) {
            return multiply(value, rate);
        } else {
            return divide(value, rate);
        }
    }

    private BigDecimal divide(BigDecimal value, BigDecimal rate) {
        return value.divide(rate, value.scale(), RoundingMode.HALF_UP);
    }

    private BigDecimal multiply(BigDecimal value, BigDecimal rate) {
        return value.multiply(rate).setScale(value.scale(), RoundingMode.HALF_UP);
    }

}
