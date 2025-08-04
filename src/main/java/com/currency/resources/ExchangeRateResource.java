package com.currency.resources;

import java.time.Instant;

import org.jboss.logging.Logger;

import com.currency.dto.common.ErrorResponse;
import com.currency.dto.request.ExchangeRequest;
import com.currency.exceptions.NoValidRateException;
import com.currency.services.contracts.IExchangeRateService;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/exchange")
@ApplicationScoped
public class ExchangeRateResource {
    
    private static final Logger LOG = Logger.getLogger(ExchangeRateResource.class);
    
    @Inject
    IExchangeRateService exchangeRateService;

    @POST
    @Path("/best-rate")
    public Uni<Response> getBestRate(@Valid ExchangeRequest request) {
        LOG.infof("Processing exchange request: %s to %s amount %s", 
                 request.sourceCurrency(), request.targetCurrency(), request.amount());
        
        return exchangeRateService.findBestExchangeRate(request)
            .onItem().transform(response -> 
                Response.ok(response).build()
            )
            .onFailure().recoverWithItem(throwable -> {
                if (throwable instanceof NoValidRateException) {
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse(
                            throwable.getMessage(), 
                            Instant.now(), 
                            "NO_VALID_RATE"
                        )).type(MediaType.APPLICATION_JSON)
                        .build();
                }
                LOG.error("Error processing exchange request", throwable);
                return Response.serverError()
                    .entity(new ErrorResponse(
                        "Internal server error", 
                        Instant.now(), 
                        "INTERNAL_ERROR"
                    ))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
            });
    }
    
    
    
}