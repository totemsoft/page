package com.totemsoft.page.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "series_data")
public class SeriesData {

    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "series_data_id")
    private Long id;

    @ToString.Include
    @NotNull
    @NaturalId
    @Column(name = "series_data_date")
    private LocalDate date;

    @NaturalId
    @NotNull
    @Column(name = "key_id")
    private Long keyId;

    @ToString.Include
    @NotNull
    @Column(name = "series_data_value")
    private BigDecimal value;

    @ToString.Include
    @NotBlank
    @Column(name = "currency_code")
    private String currency;

    @ToString.Include
    @NotBlank
    @Column(name = "base_currency")
    private String baseCurrency;

    @Column(name = "series_data_title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "key_id", insertable = false, updatable = false)
    private Key key;

}
