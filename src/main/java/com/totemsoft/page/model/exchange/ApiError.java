package com.totemsoft.page.model.exchange;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Data;

@Data
@JsonRootName("error")
public class ApiError implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Numeric error code (e.g., 101 invalid_access_key, 104 usage_limit_reached). */
    private Integer code;

    /** Machine-readable error type identifier. */
    private String type;

    /** Human-readable description of the error and suggested action. */
    private String info;

}
