package com.currency.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.currency.dto.request.ExchangeRequest;
import com.currency.dto.response.ExchangeResponse;
import com.currency.exceptions.NoValidRateException;
import com.currency.services.contracts.IExchangeRateService;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;

@QuarkusTest
class ExchangeRateResourceTest {

    @InjectMock
    IExchangeRateService exchangeRateService;

    @Test
    void testGetBestExchangeRate_Success() {
        // Arrange
        ExchangeResponse mockResponse = new ExchangeResponse(
            "TestProvider", 
            new BigDecimal("0.85"), 
            new BigDecimal("85.00"), 
            100L
        );
        
        when(exchangeRateService.findBestExchangeRate(any()))
            .thenReturn(Uni.createFrom().item(mockResponse));

        ExchangeRequest request = new ExchangeRequest("USD", "EUR", new BigDecimal("100.00"));

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/exchange/best-rate")
        .then()
            .statusCode(200)
            .body("provider", notNullValue())
            .body("rate", notNullValue())
            .body("convertedAmount", notNullValue());
    }

    @Test
    void testGetBestExchangeRate_NoValidRate() {
        // Arrange
        when(exchangeRateService.findBestExchangeRate(any()))
            .thenReturn(Uni.createFrom().failure(new NoValidRateException("No valid rates available")));

        ExchangeRequest request = new ExchangeRequest("USD", "EUR", new BigDecimal("100.00"));

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/exchange/best-rate")
        .then()
            .statusCode(400)
            .body("message", notNullValue())
            .body("errorCode", notNullValue());
    }

    @Test
    void testGetBestExchangeRate_InvalidAmount() {
        ExchangeRequest request = new ExchangeRequest("USD", "EUR", new BigDecimal("-100.00"));
        
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/exchange/best-rate")
        .then()
            .statusCode(400);
    }

    @Test
    void testGetBestExchangeRate_MissingFields() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"sourceCurrency\": \"USD\"}")
        .when()
            .post("/api/v1/exchange/best-rate")
        .then()
            .statusCode(400);
    }

    @Test
    void testGetBestExchangeRate_EmptyBody() {
        given()
            .contentType(ContentType.JSON)
            .body("{}")
        .when()
            .post("/api/v1/exchange/best-rate")
        .then()
            .statusCode(400);
    }
}