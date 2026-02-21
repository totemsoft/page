package com.totemsoft.page.model.entity.marketstack;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "eod_bar")
@IdClass(EODBarId.class)
public class EODBar {

    /** TagType name, eg for columns */
    public static final String ASSET_CLASS = "ASSET_CLASS";

    /** MIC of the exchange. */
    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @Column(name = "exchange_mic")
    private String exchange;

    /** Ticker symbol. */
    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @Column(name = "ticker_symbol")
    private String symbol;

    /** Timestamp of the bar in ISO8601 with timezone (Offset) (e.g. 2026-02-18T00:00:00+0000). */
    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @Column(name = "bar_date")
    private Instant date;

    /** Company name. */
    @Column(name = "bar_name")
    private String name;

    /** Human-friendly exchange code (for example NASDAQ). */
    @Column(name = "bar_exchange_code")
    private String exchangeCode;

    /** Asset class */
    @Column(name = "bar_asset_type")
    private String assetType;

    /** Price currency (ISO code in lower case). */
    @Column(name = "bar_price_currency")
    private String priceCurrency;

    /** Opening price for the trading session. */
    @Column(name = "bar_open")
    private BigDecimal open;

    /** Highest price of the session. */
    @Column(name = "bar_high")
    private BigDecimal high;

    /** Lowest price of the session. */
    @Column(name = "bar_low")
    private BigDecimal low;

    /** Closing price of the session. */
    @Column(name = "bar_close")
    private BigDecimal close;

    /** Traded volume during the session. */
    @Column(name = "bar_volume")
    private BigDecimal volume;

    /** High price adjusted for corporate actions. */
    @Column(name = "bar_adj_high")
    private BigDecimal adjHigh;

    /** Low price adjusted for corporate actions. */
    @Column(name = "bar_adj_low")
    private BigDecimal adjLow;

    /** Close price adjusted for corporate actions. */
    @Column(name = "bar_adj_close")
    private BigDecimal adjClose;

    /** Open price adjusted for corporate actions. */
    @Column(name = "bar_adj_open")
    private BigDecimal adjOpen;

    /** Volume adjusted for corporate actions. */
    @Column(name = "bar_adj_volume")
    private BigDecimal adjVolume;

    /** Cumulative stock split factor applied for the date. */
    @Column(name = "bar_split_factor")
    private BigDecimal splitFactor;

    /** Dividend amount per share for the date. */
    @Column(name = "bar_dividend")
    private BigDecimal dividend;

    /** Combination of exchange and symbol, eg XNAS/AAPL */
    @Transient
    public String getKeyName() {
        return exchange + '/' + symbol;
    }

}
