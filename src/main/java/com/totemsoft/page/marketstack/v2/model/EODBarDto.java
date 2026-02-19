package com.totemsoft.page.marketstack.v2.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import lombok.Data;

@Data
public class EODBarDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /** MIC of the exchange. */
    private String exchange;

    /** Ticker symbol. */
    private String symbol;

    /** Timestamp of the bar in ISO8601 with timezone (Offset) (e.g. 2026-02-18T00:00:00+0000). */
    private Instant date;

    /** Company name. */
    private String name;

    /** Human-friendly exchange code (for example NASDAQ). */
    private String exchangeCode;

    /** Asset class */
    private String assetType;

    /** Price currency (ISO code in lower case). */
    private String priceCurrency;

    /** Opening price for the trading session. */
    private BigDecimal open;

    /** Highest price of the session. */
    private BigDecimal high;

    /** Lowest price of the session. */
    private BigDecimal low;

    /** Closing price of the session. */
    private BigDecimal close;

    /** Traded volume during the session. */
    private BigDecimal volume;

    /** High price adjusted for corporate actions. */
    private BigDecimal adjHigh;

    /** Low price adjusted for corporate actions. */
    private BigDecimal adjLow;

    /** Close price adjusted for corporate actions. */
    private BigDecimal adjClose;

    /** Open price adjusted for corporate actions. */
    private BigDecimal adjOpen;

    /** Volume adjusted for corporate actions. */
    private BigDecimal adjVolume;

    /** Cumulative stock split factor applied for the date. */
    private BigDecimal splitFactor;

    /** Dividend amount per share for the date. */
    private BigDecimal dividend;

}
