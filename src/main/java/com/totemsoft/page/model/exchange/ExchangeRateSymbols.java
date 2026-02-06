package com.totemsoft.page.model.exchange;

import java.util.Map;

import lombok.Data;

@Data
public class ExchangeRateSymbols {

    private boolean success;

    private Map<String, String> symbols;

}
