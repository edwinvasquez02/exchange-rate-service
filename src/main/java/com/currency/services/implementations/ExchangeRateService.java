package com.currency.services.implementations;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.jboss.logging.Logger;
import com.currency.dto.request.ExchangeRequest;
import com.currency.dto.response.ExchangeResponse;
import com.currency.exceptions.NoValidRateException;
import com.currency.services.contracts.IExchangeProvider;
import com.currency.services.contracts.IExchangeRateService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class ExchangeRateService implements IExchangeRateService {
    
    private static final Logger LOG = Logger.getLogger(ExchangeRateService.class);
    
    @Inject
    Instance<IExchangeProvider> providerInstance;
    
    @Override
    public Uni<ExchangeResponse> findBestExchangeRate(ExchangeRequest request) {
        
        // Validar entrada
        if (request == null) {
            return Uni.createFrom().failure(new IllegalArgumentException("Exchange request cannot be null"));
        }
        
        List<IExchangeProvider> providers = providerInstance.stream().toList();
        if (providers.isEmpty()) {
            return Uni.createFrom().failure(new NoValidRateException("No exchange providers available"));
        }
        
        LOG.infof("Processing exchange request with %d providers", providers.size());
        long startTime = System.nanoTime();
        
        List<Uni<ExchangeResponse>> providerUnis = providers.stream()
                .map(provider -> callProviderSafely(provider, request))
                .toList();
        
        return Uni.combine().all().unis(providerUnis)
                .with(list -> {
                    @SuppressWarnings("unchecked")
                    List<ExchangeResponse> responses = (List<ExchangeResponse>) list;
                    long totalTimeMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
                    
                    List<ExchangeResponse> validResponses = responses.stream()
                            .filter(Objects::nonNull)
                            .filter(response -> response.rate() != null && response.rate().compareTo(BigDecimal.ZERO) > 0)
                            .toList();
                    
                    LOG.infof("Received %d valid responses out of %d total", validResponses.size(), responses.size());
                    
                    return validResponses.stream()
                            .max(Comparator.comparing(ExchangeResponse::rate))
                            .map(response -> withResponseTime(response, totalTimeMs))
                            .orElseThrow(() -> {
                                LOG.error("No valid rates available from any provider");
                                return new NoValidRateException("No valid rates available from any provider");
                            });
                });
    }
    
      private Uni<ExchangeResponse> callProviderSafely(IExchangeProvider provider, ExchangeRequest request) {
        long startTime = System.nanoTime();
        
        return provider.getExchangeRate(request)
                .onItem().transform(rate -> {
                    if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
                        LOG.warnf("Provider %s returned invalid rate: %s", provider.name(), rate);
                        return null;
                    }
                    
                    try {
                        // Ensure proper scale for monetary calculations
                        BigDecimal convertedAmount = request.amount()
                                .multiply(rate)
                                .setScale(2, RoundingMode.HALF_UP);
                        
                        ExchangeResponse response = new ExchangeResponse(
                                provider.name(),
                                rate,
                                convertedAmount,
                                TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
                        
                        LOG.debugf("Provider %s returned rate: %s", provider.name(), rate);
                        return response;
                        
                    } catch (Exception e) {
                        LOG.errorf("Error processing response from provider %s: %s", provider.name(), e.getMessage());
                        return null;
                    }
                })
                .onFailure().recoverWithItem(e -> {
                    LOG.warnf("Error calling provider %s: %s", provider.name(), e.getMessage());
                    return null;
                });
    }
    
    private ExchangeResponse withResponseTime(ExchangeResponse response, long totalTimeMs) {
        return new ExchangeResponse(
                response.provider(),
                response.rate(),
                response.convertedAmount(),
                totalTimeMs);
    }
}