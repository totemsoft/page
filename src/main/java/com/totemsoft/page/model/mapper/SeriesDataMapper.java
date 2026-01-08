package com.totemsoft.page.model.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.totemsoft.page.model.Cell;
import com.totemsoft.page.model.SeriesDataDto;
import com.totemsoft.page.model.entity.SeriesData;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SeriesDataMapper {

    List<SeriesDataDto> map(List<SeriesData> data);

    Cell<BigDecimal> map(SeriesData data);

}
