package com.currency.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.currency.dto.request.ExchangeRequest;
import com.currency.dto.response.ExchangeResponse;
import com.currency.exceptions.NoValidRateException;
import com.currency.services.contracts.IExchangeProvider;
import com.currency.services.implementations.ExchangeRateService;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.inject.Instance;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    Instance<IExchangeProvider> providerInstance;

    @InjectMocks
    ExchangeRateService exchangeRateService;

    private IExchangeProvider api1Provider;
    private IExchangeProvider api2Provider;
    private IExchangeProvider api3Provider;
    private ExchangeRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new ExchangeRequest("USD", "EUR", new BigDecimal("100.00"));
        
        // Create mock providers
        api1Provider = mock(IExchangeProvider.class);
        api2Provider = mock(IExchangeProvider.class);
        api3Provider = mock(IExchangeProvider.class);
    }
    
    private void setupProvidersWithNames() {
        // Mock provider names
        when(api1Provider.name()).thenReturn("API1");
        when(api2Provider.name()).thenReturn("API2");
        when(api3Provider.name()).thenReturn("API3");
        
        // Mock the Instance to return our mock providers
        when(providerInstance.stream()).thenReturn(Stream.of(api1Provider, api2Provider, api3Provider));
    }

    @Test
    void testFindBestExchangeRate_Success() {
        // Arrange
        setupProvidersWithNames();
        
        BigDecimal rate1 = new BigDecimal("0.85");
        BigDecimal rate2 = new BigDecimal("0.86");
        BigDecimal rate3 = new BigDecimal("0.84");

        when(api1Provider.getExchangeRate(any())).thenReturn(Uni.createFrom().item(rate1));
        when(api2Provider.getExchangeRate(any())).thenReturn(Uni.createFrom().item(rate2));
        when(api3Provider.getExchangeRate(any())).thenReturn(Uni.createFrom().item(rate3));

        // Act
        ExchangeResponse result = exchangeRateService.findBestExchangeRate(validRequest)
            .await().indefinitely();

        // Assert
        assertNotNull(result);
        assertEquals("API2", result.provider());
        assertEquals(new BigDecimal("0.86"), result.rate());
        assertEquals(0, new BigDecimal("86.00").compareTo(result.convertedAmount()));
        
        verify(api1Provider, times(1)).getExchangeRate(validRequest);
        verify(api2Provider, times(1)).getExchangeRate(validRequest);
        verify(api3Provider, times(1)).getExchangeRate(validRequest);
    }

    @Test
    void testFindBestExchangeRate_SomeProvidersFail() {
        // Arrange
        setupProvidersWithNames();
        
        BigDecimal rate1 = new BigDecimal("0.85");
        BigDecimal rate3 = new BigDecimal("0.84");

        when(api1Provider.getExchangeRate(any())).thenReturn(Uni.createFrom().item(rate1));
        when(api2Provider.getExchangeRate(any())).thenReturn(Uni.createFrom().failure(new RuntimeException("Timeout")));
        when(api3Provider.getExchangeRate(any())).thenReturn(Uni.createFrom().item(rate3));

        // Act
        ExchangeResponse result = exchangeRateService.findBestExchangeRate(validRequest)
            .await().indefinitely();

        // Assert
        assertNotNull(result);
        assertEquals("API1", result.provider());
        assertEquals(new BigDecimal("0.85"), result.rate());
        assertEquals(0, new BigDecimal("85.00").compareTo(result.convertedAmount()));
    }

    @Test
    void testFindBestExchangeRate_AllProvidersFail() {
        // Arrange
        setupProvidersWithNames();
        
        when(api1Provider.getExchangeRate(any())).thenReturn(Uni.createFrom().failure(new RuntimeException("Error")));
        when(api2Provider.getExchangeRate(any())).thenReturn(Uni.createFrom().failure(new RuntimeException("Error")));
        when(api3Provider.getExchangeRate(any())).thenReturn(Uni.createFrom().failure(new RuntimeException("Error")));

        // Act & Assert
        assertThrows(NoValidRateException.class, () -> {
            exchangeRateService.findBestExchangeRate(validRequest).await().indefinitely();
        });
    }

    @Test
    void testFindBestExchangeRate_NoProvidersAvailable() {
        // Arrange
        when(providerInstance.stream()).thenReturn(Stream.empty());

        // Act & Assert
        assertThrows(NoValidRateException.class, () -> {
            exchangeRateService.findBestExchangeRate(validRequest).await().indefinitely();
        });
    }
}