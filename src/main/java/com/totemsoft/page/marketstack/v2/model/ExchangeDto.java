package com.totemsoft.page.marketstack.v2.model;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ExchangeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mic;

    private String name;

    private String acronym;

    private String country;

    private String countryCode;

    private String city;

    private String website;

    private String operatingMic;

    private String oprtSgmt;

    private String legalEntityName;

    private String exchangeLei;

    private String marketCategoryCode;

    private String exchangeStatus;

    private LocalDate dateCreation;

    private LocalDate dateLastUpdate;

    private LocalDate dateLastValidation;

    private LocalDate dateExpiry;

    private String comments;

}
