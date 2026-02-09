package com.totemsoft.page.model.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class ExchangeRateId {

    @Column(name = "currency_date")
    private LocalDate date;

    @Column(name = "currency_base")
    private String base;

    @Column(name = "currency_code")
    private String code;

}
