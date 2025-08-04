package com.currency.api.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.currency.dto.Api3Request;
import com.currency.dto.Api3Response;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;


@RegisterRestClient(configKey = "exchange-api3")
@Path("/v2")
public interface Api3Client {
    @POST
    @Path("/exchange")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Uni<Api3Response> getRate(Api3Request request);
}