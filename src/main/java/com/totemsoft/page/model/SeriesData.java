package com.totemsoft.page.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class SeriesData {

    private long id;

    //@JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private LocalDate date;

    private BigDecimal value;

    @NotBlank
    private String title;

}
