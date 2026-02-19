package com.totemsoft.page.model.entity.marketstack;

import java.io.Serializable;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeTickerId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "exchange_mic")
    private String mic;

    @Column(name = "ticker_symbol")
    private String symbol;

}
