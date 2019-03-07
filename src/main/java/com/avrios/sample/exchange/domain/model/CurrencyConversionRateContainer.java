package com.avrios.sample.exchange.domain.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

@Data
public class CurrencyConversionRateContainer {
    static final String CURRENCY_DELIMITER = "_";

    private String xmlHash;
    private HashMap<String, BigDecimal> currencyConversionRates;
    private HashSet<String> toCurrencyCodes = new HashSet<>();
    private HashSet<String> fromCurrencyCodes = new HashSet<>();

    public Optional<BigDecimal> getConversionRate(String fromCurrencyCode, String toCurrencyCode) {
        String key = fromCurrencyCode + CURRENCY_DELIMITER + toCurrencyCode;

        return Optional.ofNullable(currencyConversionRates.get(key));
    }

    public void addConversionRate(String fromCurrencyCode, String toCurrencyCode, BigDecimal price) {
        currencyConversionRates.put(fromCurrencyCode + CURRENCY_DELIMITER + toCurrencyCode, price);
        toCurrencyCodes.add(fromCurrencyCode);
        fromCurrencyCodes.add(fromCurrencyCode);
    }

    public void emptyCurrencyCodeSets() {
        toCurrencyCodes.clear();
        fromCurrencyCodes.clear();
    }
}
