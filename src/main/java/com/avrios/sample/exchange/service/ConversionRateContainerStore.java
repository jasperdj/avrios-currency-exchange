package com.avrios.sample.exchange.service;

import com.avrios.sample.exchange.domain.model.ConversionRateContainer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * ConversionRateContainerStore, store that manages all currencyConversionRate storage specific operations.
 */
public interface ConversionRateContainerStore {
    /**
     * Get Conversion Rate Price
     *
     * @param date             date of conversion rate
     * @param fromCurrencyCode code I.E. EURO
     * @param toCurrencyCode   code I.E. USD
     * @return optional conversion rate price
     */
    Optional<BigDecimal> getConversionRate(LocalDate date, String fromCurrencyCode, String toCurrencyCode);

    /**
     * Add conversion rate container
     *
     * @param container container to add
     * @return boolean indicating whether adding was successful
     */
    boolean addConversionRateContainer(ConversionRateContainer container, LocalDate date);

    /**
     * @return 'from' currency codes that currently exists in the store
     */
    HashSet<String> getFromCurrencyCodes();

    /**
     * @return 'to' currency codes that currently exists in the store
     */
    HashSet<String> getToCurrencyCodes();


    /**
     * @return the date that is associated to the head, a.k.a. the newest/oldest date in the buffer.
     */
    LocalDate getHeadDate();


    /**
     * @return the dates associated to the empty slots in the buffer.
     */
    List<LocalDate> getMissingDates();
}