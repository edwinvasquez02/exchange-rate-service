package com.currency.dto.common;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record DataResponse(
    @JsonProperty("total")
    BigDecimal total
) {}