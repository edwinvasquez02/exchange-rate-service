package com.currency.services.contracts;

import java.math.BigDecimal;

import com.currency.dto.request.ExchangeRequest;

import io.smallrye.mutiny.Uni;

public interface IExchangeProvider {
    String name();
    Uni<BigDecimal> getExchangeRate(ExchangeRequest request);
}
