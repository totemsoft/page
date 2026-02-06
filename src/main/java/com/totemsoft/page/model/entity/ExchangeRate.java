package com.totemsoft.page.model.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import jakarta.persistence.Column;
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
@Table(name = "exchange_rate")
@IdClass(ExchangeRateId.class)
public class ExchangeRate {

    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @Column(name = "currency_code")
    private String code;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @Column(name = "currency_date")
    private LocalDate date;

    @Column(name = "currency_rate")
    private BigDecimal rate;

    @Column(name = "currency_timestamp")
    private Timestamp timestamp;

    @Column(name = "base_currency")
    private String baseCurrency;

}
