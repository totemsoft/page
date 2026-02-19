package com.totemsoft.page.model.entity.marketstack;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exchange_ticker")
@IdClass(ExchangeTickerId.class)
public class ExchangeTicker {

    /** TagType name, eg for columns */
    public static final String EXCHANGE_TICKER = "EXCHANGE_TICKER";

    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @Column(name = "exchange_mic")
    private String mic;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @Column(name = "ticker_symbol")
    private String symbol;

    @Column(name = "ticker_name")
    private String name;

    @Convert(converter = org.hibernate.type.YesNoConverter.class)
    @Column(name = "ticker_has_intraday")
    private Boolean hasIntraday;

    @Convert(converter = org.hibernate.type.YesNoConverter.class)
    @Column(name = "ticker_has_eod")
    private Boolean hasEod;

    @Convert(converter = org.hibernate.type.YesNoConverter.class)
    @Column(name = "ticker_base")
    private Boolean base;

}
