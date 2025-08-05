package com.currency.utils;

import java.util.Set;

public class CurrencyUtils {
    
    private static final Set<String> CURRENCIES = Set.of(
        "USD", "EUR", "GBP", "JPY", "CHF", "CAD", "AUD", "NZD", "DOP"
    );
    
    public static boolean isValidCurrency(String currency) {
        return currency != null && CURRENCIES.contains(currency.toUpperCase());
    }
    
}