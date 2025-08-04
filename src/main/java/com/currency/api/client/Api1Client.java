package com.currency.api.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.currency.dto.Api1Request;
import com.currency.dto.Api1Response;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@RegisterRestClient(configKey = "exchange-api1")
@Path("/v1")
public interface Api1Client {
    @POST
    @Path("/rates")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Uni<Api1Response> getRate(Api1Request request);
}