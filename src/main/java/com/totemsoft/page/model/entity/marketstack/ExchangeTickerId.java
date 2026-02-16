package com.totemsoft.page.model.entity.marketstack;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeTickerId {

    @Column(name = "exchange_mic")
    private String mic;

    @Column(name = "ticker_symbol")
    private String symbol;

}
