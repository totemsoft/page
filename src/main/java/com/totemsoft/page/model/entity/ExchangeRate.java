package com.totemsoft.page.model.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "exchange_rate")
@IdClass(ExchangeRateId.class)
public class ExchangeRate {

    /** TagType name, eg for columns */
    public static final String CURRENCY_BASE = "CURRENCY_BASE";

    /** TagType name, eg for rows */
    public static final String CURRENCY_CODE = "CURRENCY_CODE";

    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @Column(name = "currency_date")
    private LocalDate date;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @Column(name = "currency_base")
    private String base;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @Column(name = "currency_code")
    private String code;

    @NotNull
    @Column(name = "currency_rate")
    private BigDecimal rate;

    @NotNull
    @Column(name = "currency_timestamp")
    private Timestamp timestamp;

    /** Combination of base currency and currency code, eg EUR/USD */
    @Transient
    public String getName() {
        return base + '/' + code;
    }

}
