package com.avrios.sample.exchange.domain.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Optional;

@Data
public class CurrencyConversionRateContainer {
    static final String CURRENCY_DELIMITER = "_";

    private String xmlHash;
    private HashMap<String, BigDecimal> currencyConversionRates;

    public Optional<BigDecimal> getConversionRate(String fromCurrencyCode, String toCurrencyCode) {
        String key = fromCurrencyCode + CURRENCY_DELIMITER + toCurrencyCode;

        return Optional.ofNullable(currencyConversionRates.get(key));
    }

    public void addConversionRate(String fromCurrencyCode, String toCurrencyCode, BigDecimal price) {
        currencyConversionRates.put(fromCurrencyCode + CURRENCY_DELIMITER + toCurrencyCode, price);
    }
}
