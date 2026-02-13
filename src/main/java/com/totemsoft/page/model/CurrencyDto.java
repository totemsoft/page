package com.totemsoft.page.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class CurrencyDto {

    @EqualsAndHashCode.Include
    @ToString.Include
    private String code;

    private String title;

    private Boolean base;

}
