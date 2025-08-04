package com.currency.api.client;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@RegisterRestClient(configKey = "exchange-api2")
@Path("/xml-api")
public interface Api2Client {
    @POST
    @Path("/convert")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    Uni<String> getRate(String xmlRequest);
}