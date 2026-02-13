package com.totemsoft.page.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.totemsoft.page.model.ColumnDef.CellFormatterEnum;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class SeriesDataDto {

    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    //@JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private LocalDate date;

    private BigDecimal value;

    private String baseCurrency;

    private String currency;

    @NotBlank
    private String title;

    /**
     * @return true if baseCurrency same as currency
     */
    public boolean sameCurrency() {
        return baseCurrency.equals(currency);
    }

    public static List<ColumnDef> columns() {
        return List.of(
            ColumnDef.builder()
                .key("id")
                .label("ID")
                //.hidden(true) // TODO: fix dataTable.doBeforeLoadData insertColumn issue
                .formatter(CellFormatterEnum.NUMBER.name().toLowerCase())
                .className(CssClasName.RIGHT)
                .width(50)
                .build(),
            ColumnDef.builder()
                .key("date")
                .label("Date")
                //.formatter(CellFormatterEnum.DATE.name().toLowerCase())
                //.dateOptions(ColumnDef.DATE_OPTIONS)
                .build(),
            ColumnDef.builder()
                .key("value")
                .label("Value")
                .formatter(CellFormatterEnum.CURRENCY.name().toLowerCase())
                .className(CssClasName.RIGHT)
                .build(),
            ColumnDef.builder()
                .key("title")
                .label("Name")
                .build()
            );
    }

}
