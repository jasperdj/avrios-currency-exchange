package com.avrios.sample.exchange.service;

import com.avrios.sample.exchange.domain.model.CurrencyConversionRateContainer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * CurrencyConversionRateContainerStore, store that manages all currencyConversionRate storage specific operations.
 */
public interface CurrencyConversionRateContainerStore {
    /**
     * Get Conversion Rate Price
     *
     * @param date             date of conversion rate
     * @param fromCurrencyCode code I.E. EURO
     * @param toCurrencyCode   code I.E. USD
     * @return optional conversion rate price
     */
    Optional<BigDecimal> getConversionRatePrice(LocalDate date, String fromCurrencyCode, String toCurrencyCode);

    /**
     * Add conversion rate container
     *
     * @param container container to add
     * @return boolean indicating whether adding was successful
     */
    boolean addConversionRateContainer(CurrencyConversionRateContainer container);

    /**
     * @return 'from' currency codes that currently exists in the store
     */
    List<String> getFromCurrencyCodes();

    /**
     * @return 'to' currency codes that currently exists in the store
     */
    List<String> getToCurrencyCodes();
}