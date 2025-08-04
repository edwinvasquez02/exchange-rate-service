package com.currency.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.xml.bind.annotation.XmlRootElement;

@RegisterForReflection
@XmlRootElement(name = "exchangeResponse")
public record Api2Response(
    @JacksonXmlProperty(localName = "Result")
    BigDecimal result
) {}
