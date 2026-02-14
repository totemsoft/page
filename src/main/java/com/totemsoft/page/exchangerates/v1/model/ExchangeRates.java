package com.totemsoft.page.exchangerates.v1.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import lombok.Data;

@Data
public class ExchangeRates implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;

    private boolean historical;

    private LocalDate date;

    private long timestamp;

    private String base;

    private Map<String, BigDecimal> rates;

}
