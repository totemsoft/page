package com.totemsoft.page.model.exchange;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import lombok.Data;

@Data
public class ExchangeRates {

    private boolean success;

    private boolean historical;

    private LocalDate date;

    private long timestamp;

    private String base;

    private Map<String, BigDecimal> rates;

}
