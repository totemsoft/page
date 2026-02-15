package com.totemsoft.page.marketstack.v2.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ExchangeTickerDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Ticker symbol. */
    private String symbol;

    /** Company or instrument name. */
    private String name;

    /** Indicates if intraday data is available. */
    private Boolean hasIntraday;

    /** Indicates if end-of-day data is available. */
    private Boolean hasEod;

}
