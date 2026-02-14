package com.totemsoft.page.model.entity.marketstack;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "exchange")
public class Exchange {

    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @Column(name = "exchange_mic")
    private String mic;

    @Column(name = "exchange_name")
    private String name;

    @Column(name = "exchange_acronym")
    private String acronym;

    @Column(name = "exchange_country")
    private String country;

    @Column(name = "exchange_country_code")
    private String countryCode;

    @Column(name = "exchange_city")
    private String city;

    @Column(name = "exchange_website")
    private String website;

    @Column(name = "exchange_operating_mic")
    private String operatingMic;

    @Column(name = "exchange_oprt_sgmt")
    private String oprtSgmt;

    @Column(name = "exchange_legal_entity_name")
    private String legalEntityName;

    @Column(name = "exchange_exchange_lei")
    private String exchangeLei;

    @Column(name = "exchange_market_category_code")
    private String marketCategoryCode;

    @Column(name = "exchange_exchange_status")
    private String exchangeStatus;

    @Column(name = "exchange_date_creation")
    private LocalDate dateCreation;

    @Column(name = "exchange_date_last_update")
    private LocalDate dateLastUpdate;

    @Column(name = "exchange_date_last_validation")
    private LocalDate dateLastValidation;

    @Column(name = "exchange_date_expiry")
    private LocalDate dateExpiry;

    @Column(name = "exchange_comments")
    private String comments;

}
