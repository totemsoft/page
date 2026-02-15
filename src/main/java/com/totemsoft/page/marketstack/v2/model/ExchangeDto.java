package com.totemsoft.page.marketstack.v2.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.totemsoft.page.model.ColumnDef;
import com.totemsoft.page.model.ColumnDef.CellFormatterEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class ExchangeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @EqualsAndHashCode.Include
    @ToString.Include
    private String mic;

    @ToString.Include
    private String name;

    private String acronym;

    private String country;

    private String countryCode;

    @ToString.Include
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

    private Boolean base;

    public static List<ColumnDef> columns(boolean editable) {
        return List.of(
            ColumnDef.builder()
                .key("mic")
                .label("MIC")
                .width(100)
                .build(),
            ColumnDef.builder()
                .key("name")
                .label("Name")
                .build(),
            ColumnDef.builder()
                .key("city")
                .label("City")
                .build(),
            ColumnDef.builder()
                .key("base")
                .label("Base")
                .formatter(editable ? CellFormatterEnum.CHECKBOX.name().toLowerCase() : null)
                .build()
            );
    }

}
