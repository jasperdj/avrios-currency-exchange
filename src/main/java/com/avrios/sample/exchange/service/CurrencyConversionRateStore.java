package com.avrios.sample.exchange.service;

import com.avrios.sample.exchange.domain.model.CurrencyConversionRate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * CurrencyConversionRateStore, the data store where all the currency conversion rates are stored.
 */
public interface CurrencyConversionRateStore {
    /**
     * Get Conversion Rate Price
     *
     * @param date             date of conversion rate
     * @param fromCurrencyCode code I.E. EURO
     * @param toCurrencyCode   code I.E. USD
     * @return optional conversion rate price
     */
    Optional<BigDecimal> getConversionRatePrice(LocalDate date, String fromCurrencyCode, String toCurrencyCode);

    void addConversionRate(CurrencyConversionRate currencyConversionRate);

    void addConversionRates(List<CurrencyConversionRate> currencyConversionRateList);
}