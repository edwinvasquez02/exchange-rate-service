package com.currency.services.implementations;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.currency.api.client.Api3Client;
import com.currency.dto.Api3Request;
import com.currency.dto.request.ExchangeRequest;
import com.currency.services.contracts.IExchangeProvider;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Api3Provider implements IExchangeProvider {
    
    private static final Logger LOG = Logger.getLogger(Api1Provider.class);
    
    @RestClient
    Api3Client apiClient;
    
    @Override
    public String name() { return "API3"; }
    
    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @Retry(maxRetries = 2, delay = 500)
    @Fallback(fallbackMethod = "fallbackRate")
    public Uni<BigDecimal> getExchangeRate(ExchangeRequest request) {

        if (request == null || request.sourceCurrency() == null || 
            request.targetCurrency() == null || request.amount() == null) {
            LOG.error("Invalid request parameters for API3");
            return Uni.createFrom().item((BigDecimal) null);
        }
        
        Api3Request apiRequest = new Api3Request(
            request.sourceCurrency(),
            request.targetCurrency(),
            request.amount()
        );
        
        return apiClient.getRate(apiRequest)
            .onItem().transform(response -> response.data().total())
            .onFailure().recoverWithItem(e -> {
                LOG.errorf("Error calling API3: %s", e.getMessage());
                return null;
            });
    }
    
    public Uni<BigDecimal> fallbackRate(ExchangeRequest request) {
        LOG.warn("Using fallback for API3");
        return Uni.createFrom().item((BigDecimal) null);
    }
}