package com.totemsoft.page.model.entity.exchangerates;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRateId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "currency_date")
    private LocalDate date;

    @Column(name = "currency_base")
    private String base;

    @Column(name = "currency_code")
    private String code;

}
