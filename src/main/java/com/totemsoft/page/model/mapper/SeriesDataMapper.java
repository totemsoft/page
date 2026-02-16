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

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SeriesDataMapper {

    List<SeriesDataDto> mapSeriesData(Collection<SeriesData> data);
    Cell<BigDecimal> mapSeriesData(SeriesData data);

    default Cell<BigDecimal> mapSeriesData(SeriesData sd, Optional<ExchangeRate> exchangeRate) {
        final var cell = this.mapSeriesData(sd);
        exchangeRate.ifPresent(er -> cell.setValue(convert(sd, er)));
        return cell;
    }

    default List<SeriesDataDto> mapSeriesData(Collection<SeriesData> data, Optional<ExchangeRate> exchangeRate) {
        final var result = this.mapSeriesData(data);
        exchangeRate.ifPresent(er -> result.forEach(sd -> sd.setValue(convert(sd, er))));
        return result;
    }

    private BigDecimal convert(SeriesData sd, ExchangeRate exchangeRate) {
        // TODO: SeriesData baseCurrency/currency VS ExchangeRate base/code
        if (sd.sameCurrency()) {
            return multiply(sd.getValue(), exchangeRate.getRate());
        } else {
            return divide(sd.getValue(), exchangeRate.getRate());
        }
    }

    private BigDecimal convert(SeriesDataDto sd, ExchangeRate exchangeRate) {
        // TODO: SeriesData baseCurrency/currency VS ExchangeRate base/code
        if (sd.sameCurrency()) {
            return multiply(sd.getValue(), exchangeRate.getRate());
        } else {
            return divide(sd.getValue(), exchangeRate.getRate());
        }
    }

    private BigDecimal divide(BigDecimal value, BigDecimal rate) {
        return value.divide(rate, value.scale(), RoundingMode.HALF_UP);
    }

    private BigDecimal multiply(BigDecimal value, BigDecimal rate) {
        return value.multiply(rate).setScale(value.scale(), RoundingMode.HALF_UP);
    }

}
