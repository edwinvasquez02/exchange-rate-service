package com.currency.dto;

import com.currency.dto.common.DataResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record Api3Response(
    @JsonProperty("statusCode")
    Integer statusCode,
    
    @JsonProperty("message")
    String message,
    
    @JsonProperty("data")
    DataResponse data
) {
}