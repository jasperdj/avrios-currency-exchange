package com.avrios.sample.exchange.service;

import com.avrios.sample.exchange.domain.model.CurrencyConversionRateContainer;
import com.avrios.sample.exchange.util.LocalDateRingBuffer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

@Service("CurrencyConversionRateContainerStore")
public class CurrencyConversionRateContainerStoreImpl implements CurrencyConversionRateContainerStore {
    private LocalDateRingBuffer<CurrencyConversionRateContainer> buffer;
    private HashSet<String> fromCurrencyCodes = new HashSet<>();
    private HashSet<String> toCurrencyCodes = new HashSet<>();
    // todo: setup configurationProperties
    @Value("${service.CurrencyConversionRateContainerStore.sizeInDays}")
    private Integer sizeInDays = 90;

    public CurrencyConversionRateContainerStoreImpl() {
        buffer = new LocalDateRingBuffer<>(sizeInDays, LocalDate.now().minusDays(1));
    }

    @Override
    public Optional<BigDecimal> getConversionRate(LocalDate date, String fromCurrencyCode, String toCurrencyCode) {
        Optional<CurrencyConversionRateContainer> optionalItem = buffer.getItemAtDate(date);

        if (optionalItem.isPresent()) {
            return optionalItem.get().getConversionRate(fromCurrencyCode, toCurrencyCode);
        }

        return Optional.empty();
    }

    @Override
    public boolean addConversionRateContainer(LocalDate date, CurrencyConversionRateContainer container) {
        Optional<Integer> optionalIndex = buffer.canAddOnDate(date);

        if (optionalIndex.isPresent()) {
            fromCurrencyCodes.addAll(container.getFromCurrencyCodes());
            toCurrencyCodes.addAll(container.getToCurrencyCodes());
            container.emptyCurrencyCodeSets();

            buffer.add(optionalIndex.get(), container);

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