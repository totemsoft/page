package com.totemsoft.page.model.exchange;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;

@Data
public class ExchangeRateSymbols implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;

    private Map<String, String> symbols;

}
