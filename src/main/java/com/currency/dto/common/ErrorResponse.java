package com.currency.dto.common;

import java.time.Instant;

public record ErrorResponse(
    String message,
    Instant timestamp,
    String errorCode
) {}