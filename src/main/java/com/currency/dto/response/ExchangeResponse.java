package com.currency.dto.response;

import java.math.BigDecimal;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ExchangeResponse(
    String provider,
    BigDecimal rate,
    BigDecimal convertedAmount,
    long responseTimeMs
) {}