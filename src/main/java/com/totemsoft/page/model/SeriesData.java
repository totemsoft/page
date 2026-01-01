package com.totemsoft.page.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
public class SeriesData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "series_id")
    private long id;

    //@JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @Column(name = "series_date")
    private LocalDate date;

    @Column(name = "series_value")
    private BigDecimal value;

    @NotBlank
    @Column(name = "series_title")
    private String title;

}
