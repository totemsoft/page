package com.totemsoft.page.model;

import java.util.List;

import com.totemsoft.page.model.entity.SeriesData;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeriesDataResult {

    private List<ColumnDef> columns;

    private List<SeriesData> records;

}
