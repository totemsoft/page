package com.totemsoft.page.marketstack.v2.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer limit;

    private Integer offset;

    private Integer count;

    private Integer total;

}
