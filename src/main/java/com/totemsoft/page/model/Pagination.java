package com.totemsoft.page.model;

import com.totemsoft.page.marketstack.v2.model.PaginationDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Pagination extends PaginationDto {

    private static final long serialVersionUID = 1L;

    private String sort;

    private String dir;

}
