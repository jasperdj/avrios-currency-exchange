package com.avrios.sample.exchange.service;

import com.avrios.sample.exchange.domain.model.CurrencyConversionRateContainer;
import com.avrios.sample.exchange.util.LocalDateRingBuffer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

// Todo write BDD tests for this class
// Todo: Write BDD tests for model

@Service("CurrencyConversionRateContainerStore")
public class CurrencyConversionRateContainerStoreImpl implements CurrencyConversionRateContainerStore {
    private LocalDateRingBuffer<CurrencyConversionRateContainer> store;
    private HashSet<String> fromCurrencyCodes = new HashSet<>();
    private HashSet<String> toCurrencyCodes = new HashSet<>();

    @Override
    public Optional<BigDecimal> getConversionRate(LocalDate date, String fromCurrencyCode, String toCurrencyCode) {
        Optional<CurrencyConversionRateContainer> optionalItem = store.getItemAtDate(date);

        if (optionalItem.isPresent()) {
            return optionalItem.get().getConversionRate(fromCurrencyCode, toCurrencyCode);
        }

        return Optional.empty();
    }

    @Override
    public boolean addConversionRateContainer(LocalDate date, CurrencyConversionRateContainer container) {
        Optional<Integer> optionalIndex = store.canAddOnDate(date);

        if (optionalIndex.isPresent()) {
            fromCurrencyCodes.addAll(container.getFromCurrencyCodes());
            fromCurrencyCodes.addAll(container.getToCurrencyCodes());
            container.emptyCurrencyCodeSets();

            store.add(optionalIndex.get(), container);

            return true;
        }

        return false;
    }

    @Override
    public HashSet<String> getFromCurrencyCodes() {
        return fromCurrencyCodes;
    }

    @Override
    public HashSet<String> getToCurrencyCodes() {
        return toCurrencyCodes;
    }
}
