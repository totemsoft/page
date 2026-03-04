package com.totemsoft.page.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.totemsoft.page.model.ColumnDef.CellFormatterEnum;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SeriesDataDto {

    @EqualsAndHashCode.Include
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

    public static List<ColumnDef> columns(Optional<CellFormatterEnum> formatter) {
        return List.of(
            ColumnDef.builder()
                .key("id")
                .label("ID")
                .className(CssClasName.WIDTH0)
                .build(),
            ColumnDef.builder()
                .key("value")
                .label("Value")
                .formatter(formatter.orElse(CellFormatterEnum.NUMBER).name().toLowerCase())
                .className(CssClasName.RIGHT)
                .build(),
            ColumnDef.builder()
                .key("title")
                .label("Name")
                .build()
            );
    }

}
