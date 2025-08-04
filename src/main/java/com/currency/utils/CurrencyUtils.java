package com.currency.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

public class CurrencyUtils {
    
    private static final Set<String> CURRENCIES = Set.of(
        "USD", "EUR", "GBP", "JPY", "CHF", "CAD", "AUD", "NZD", "DOP"
    );
    
    public static boolean isValidCurrency(String currency) {
        return currency != null && CURRENCIES.contains(currency.toUpperCase());
    }
    
    public static BigDecimal calculateConvertedAmount(BigDecimal amount, BigDecimal rate) {
        if (amount == null || rate == null) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
    
    public static BigDecimal calculateRate(BigDecimal originalAmount, BigDecimal convertedAmount) {
        if (originalAmount == null || convertedAmount == null || originalAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return convertedAmount.divide(originalAmount, 6, RoundingMode.HALF_UP);
    }
    
    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return amount.setScale(2, RoundingMode.HALF_UP).toString();
    }
}