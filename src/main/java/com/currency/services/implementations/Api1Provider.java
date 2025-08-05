package com.currency.services.implementations;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.currency.api.client.Api1Client;
import com.currency.dto.Api1Request;
import com.currency.dto.Api1Response;
import com.currency.dto.request.ExchangeRequest;
import com.currency.services.contracts.IExchangeProvider;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Api1Provider implements IExchangeProvider {

    
    private static final Logger LOG = Logger.getLogger(Api1Provider.class);
    
    @RestClient
    Api1Client apiClient;
    
    @Override
    public String name() { return "API1"; }
    
    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @Retry(maxRetries = 2, delay = 500)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 1000)
    public Uni<BigDecimal> getExchangeRate(ExchangeRequest request) {
        if (request == null || request.sourceCurrency() == null || 
            request.targetCurrency() == null || request.amount() == null) {
            LOG.error("Invalid request parameters for API1");
            return Uni.createFrom().item((BigDecimal) null);
        }
        
        Api1Request apiRequest = new Api1Request(
            request.sourceCurrency(),
            request.targetCurrency(),
            request.amount()
        );
        
        return apiClient.getRate(apiRequest)
            .onItem().transform(Api1Response::rate)
            .onFailure().recoverWithItem(e -> {
                LOG.errorf("Error calling API1: %s", e.getMessage());
                return null;
            });
    }
}
