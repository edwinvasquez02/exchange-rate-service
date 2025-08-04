package com.currency.services.implementations;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.currency.api.client.Api2Client;
import com.currency.dto.Api2Request;
import com.currency.dto.Api2Response;
import com.currency.dto.request.ExchangeRequest;
import com.currency.services.contracts.IExchangeProvider;
import com.currency.utils.XmlUtils;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.xml.bind.JAXBException;

@ApplicationScoped
public class Api2Provider implements IExchangeProvider {
    
    private static final Logger LOG = Logger.getLogger(Api2Provider.class);
    
    @RestClient
    Api2Client apiClient;
    
    @Override
    public String name() {
        return "API2";
    }
    
    @Timeout(value = 3, unit = ChronoUnit.SECONDS)
    @Retry(maxRetries = 3, delay = 500)
    public Uni<BigDecimal> getExchangeRate(ExchangeRequest request) {
        
        // Validar entrada
        if (request == null || request.sourceCurrency() == null || 
            request.targetCurrency() == null || request.amount() == null) {
            LOG.error("Invalid request parameters");
            return Uni.createFrom().nullItem();
        }
        
        Api2Request apiRequest = new Api2Request(
                request.sourceCurrency(),
                request.targetCurrency(),
                request.amount());
        
        try {
            String xmlRequest = XmlUtils.objectToXml(apiRequest, Api2Request.class);
            LOG.debugf("Sending XML request to API2: %s", xmlRequest);
            
            return apiClient.getRate(xmlRequest)
                    .onItem().transform(this::parseXmlResponse)
                    .onFailure().recoverWithItem(e -> {
                        LOG.errorf("Error calling API2: %s", e.getMessage());
                        return null;
                    });
                    
        } catch (JAXBException e) {
           LOG.errorf("Failed to convert request to XML: %s", e.getMessage());
            return Uni.createFrom().item((BigDecimal) null);
        }
    }
    
    private BigDecimal parseXmlResponse(String xml) {
        try {
            if (xml == null || xml.trim().isEmpty()) {
                LOG.warn("Received empty XML response from API2");
                return null;
            }
            
            Api2Response response = XmlUtils.xmlToObject(xml, Api2Response.class);
            if (response == null || response.result() == null) {
                LOG.warn("Parsed response is null or missing result");
                return null;
            }
            
            LOG.debugf("Successfully parsed API2 response: %s", response.result());
            return response.result();
            
        } catch (Exception e) {
            LOG.errorf("Failed to parse XML response: %s. XML content: %s", e.getMessage(), xml);
            throw new RuntimeException("Failed to parse XML response", e);
        }
    }
}