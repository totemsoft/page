package com.totemsoft.page.model.entity.marketstack;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class ExchangeTickerId {

    @Column(name = "exchange_mic")
    private String mic;

    @Column(name = "ticker_symbol")
    private String symbol;

}
