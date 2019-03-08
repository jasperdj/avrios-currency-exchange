package com.avrios.sample.exchange.domain.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

@Data
public class ConversionRateContainer {
    static final String CURRENCY_DELIMITER = "_";

    private HashMap<String, BigDecimal> currencyConversionRates = new HashMap<>();
    private HashSet<String> toCurrencyCodes = new HashSet<>();
    private HashSet<String> fromCurrencyCodes = new HashSet<>();

    public ConversionRateContainer() {
    }

    public Optional<BigDecimal> getConversionRate(String fromCurrencyCode, String toCurrencyCode) {
        String key = fromCurrencyCode + CURRENCY_DELIMITER + toCurrencyCode;

        return Optional.ofNullable(currencyConversionRates.get(key));
    }

    public void addConversionRate(String fromCurrencyCode, String toCurrencyCode, BigDecimal rate) {
        currencyConversionRates.put(fromCurrencyCode + CURRENCY_DELIMITER + toCurrencyCode, rate);
        toCurrencyCodes.add(toCurrencyCode);
        fromCurrencyCodes.add(fromCurrencyCode);
    }

    public void emptyCurrencyCodeSets() {
        toCurrencyCodes.clear();
        fromCurrencyCodes.clear();
    }
}
