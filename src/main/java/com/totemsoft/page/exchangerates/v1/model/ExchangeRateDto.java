package com.totemsoft.page.exchangerates.v1.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ExchangeRateDto {

    private LocalDate date;

    private String base;

    private String code;

    private BigDecimal rate;

    private Timestamp timestamp;

    public String getKeyName() {
        return base + '/' + code;
    }

}
