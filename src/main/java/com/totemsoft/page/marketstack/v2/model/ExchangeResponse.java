package com.totemsoft.page.marketstack.v2.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ExchangeResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private PaginationDto pagination;

    private List<ExchangeDto> data;

}
