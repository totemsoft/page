package com.totemsoft.page.model.entity.marketstack;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class EODBarId implements Serializable {

    private static final long serialVersionUID = 1L;

    /** MIC of the exchange. */
    @Column(name = "exchange_mic")
    private String exchange;

    /** Ticker symbol. */
    @Column(name = "ticker_symbol")
    private String symbol;

    /** Timestamp of the bar in ISO8601 with timezone (Offset) (e.g. 2026-02-18T00:00:00+0000). */
    @Column(name = "bar_date")
    private Instant date;

}
