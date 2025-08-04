package com.currency.dto;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;


@RegisterForReflection
public record Api1Response(
    @JsonProperty("rate")
    BigDecimal rate
) {}