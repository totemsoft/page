package com.totemsoft.page.marketstack.v2.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ExchangeTickerResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private PaginationDto pagination;

    private ExchangeTickerData data;

}
