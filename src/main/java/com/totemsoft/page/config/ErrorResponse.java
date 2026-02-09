package com.totemsoft.page.config;

import org.springframework.http.HttpStatusCode;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    @JsonProperty("status")
    private HttpStatusCode status;

    @JsonProperty("message")
    private String message;

}
