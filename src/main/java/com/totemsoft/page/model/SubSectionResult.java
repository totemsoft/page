package com.totemsoft.page.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubSectionResult {

    private List<ColumnDef> columns;

    private List<SeriesDataDto> records;

}
