package com.currency.services.contracts;

import com.currency.dto.request.ExchangeRequest;
import com.currency.dto.response.ExchangeResponse;

import io.smallrye.mutiny.Uni;

public interface IExchangeRateService { 
    Uni<ExchangeResponse> findBestExchangeRate(ExchangeRequest request);
}