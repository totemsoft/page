package com.totemsoft.page.marketstack.v2.model;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class ExchangeMicEodData extends ExchangeDto {

    private static final long serialVersionUID = 1L;

    private List<EODBarDto> eod;

}
