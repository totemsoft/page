package com.totemsoft.page.model;

import lombok.Data;

@Data
public class LoadDto<T> {

    private T id;

    private int limit;

    private int remainder;

}
