package com.totemsoft.page.model.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Data;

@Data
@JsonRootName("error")
public class Error {

    /** Numeric error code (e.g., 101 invalid_access_key, 104 usage_limit_reached). */
    @JsonProperty("code")
    private Integer code;

    /** Machine-readable error type identifier. */
    @JsonProperty("type")
    private String type;

    /** Human-readable description of the error and suggested action. */
    @JsonProperty("info")
    private String info;

}
