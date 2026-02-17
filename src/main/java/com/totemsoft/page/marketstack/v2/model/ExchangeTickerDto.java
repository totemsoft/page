package com.totemsoft.page.marketstack.v2.model;

import java.io.Serializable;
import java.util.List;

import com.totemsoft.page.model.ColumnDef;
import com.totemsoft.page.model.ColumnDef.CellFormatterEnum;
import com.totemsoft.page.model.CssClasName;

import lombok.Data;

@Data
public class ExchangeTickerDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mic;

    /** Ticker symbol. */
    private String symbol;

    /** Company or instrument name. */
    private String name;

    /** Indicates if intraday data is available. */
    private Boolean hasIntraday;

    /** Indicates if end-of-day data is available. */
    private Boolean hasEod;

    private Boolean base;

    public static List<ColumnDef> columns(boolean editable) {
        return List.of(
            ColumnDef.builder()
                .key("mic")
                .label("MIC")
                //.width(50)
                .className(CssClasName.WIDTH0)
                .build(),
            ColumnDef.builder()
                .key("symbol")
                .label("Symbol")
                .width(120)
                .build(),
            ColumnDef.builder()
                .key("name")
                .label("Name")
                .build(),
            ColumnDef.builder()
                .key("base")
                .label("Base")
                .width(50)
                .formatter(editable ? CellFormatterEnum.CHECKBOX.name().toLowerCase() : null)
                .build()
            );
    }

}
