package com.totemsoft.page.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Row {

    @JsonValue
    private Map<String, Cell<?>> cells;

}
