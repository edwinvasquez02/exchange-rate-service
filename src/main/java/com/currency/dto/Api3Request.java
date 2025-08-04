package com.currency.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record Api3Request(
    @JsonProperty("sourceCurrency") String sourceCurrency,
    @JsonProperty("targetCurrency") String targetCurrency,
    @JsonProperty("quantity") BigDecimal quantity
) {}