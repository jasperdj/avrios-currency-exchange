package com.avrios.sample.exchange.service;

import com.avrios.sample.exchange.domain.model.ConversionRateContainer;
import com.avrios.sample.exchange.util.LocalDateRingBuffer;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Optional;

@Service("CurrencyConversionRateContainerStore")
public class ConversionRateContainerStoreImpl implements ConversionRateContainerStore {
    private LocalDateRingBuffer<ConversionRateContainer> buffer;
    private HashSet<String> fromCurrencyCodes = new HashSet<>();
    private HashSet<String> toCurrencyCodes = new HashSet<>();
    @Getter
    private Observable headMovedUp;
    // todo: setup configurationProperties
    @Value("${service.ConversionRateContainerStore.sizeInDays}")
    private Integer sizeInDays = 90;

    public ConversionRateContainerStoreImpl() {
        buffer = new LocalDateRingBuffer<>(sizeInDays, LocalDate.now().minusDays(10));
    }

    @Override
    public Optional<BigDecimal> getConversionRate(LocalDate date, String fromCurrencyCode, String toCurrencyCode) {
        Optional<ConversionRateContainer> optionalItem = buffer.getItemAtDate(date);

        if (optionalItem.isPresent()) {
            return optionalItem.get().getConversionRate(fromCurrencyCode, toCurrencyCode);
        }

        return Optional.empty();
    }

    @Override
    public boolean addConversionRateContainer(ConversionRateContainer container, LocalDate date) {
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

    @Override
    public LocalDate getHeadDate() {
        return buffer.getHeadDate();
    }

    @Override
    public List<LocalDate> getMissingDates() {
        return buffer.getEmptyItemSlotDates();
    }

}