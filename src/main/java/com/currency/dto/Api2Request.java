package com.currency.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.xml.bind.annotation.XmlRootElement;

@RegisterForReflection
@XmlRootElement(name = "exchangeRequest")
public record Api2Request(
    @JacksonXmlProperty(localName = "From")
    String from,
    
    @JacksonXmlProperty(localName = "To")
    String to,
    
    @JacksonXmlProperty(localName = "Amount")
    BigDecimal amount
) {}