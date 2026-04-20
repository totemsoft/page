package com.totemsoft.page.model;

import java.time.LocalDate;

import lombok.Data;

@Data
public class LoadDto<T> {

    private T id;

    private LocalDate date;

    private int limit;

    private int remainder;

}
