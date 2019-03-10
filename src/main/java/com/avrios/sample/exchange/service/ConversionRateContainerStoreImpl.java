package com.avrios.sample.exchange.service;

import com.avrios.sample.exchange.configuration.AppProperties;
import com.avrios.sample.exchange.domain.model.ConversionRateContainer;
import com.avrios.sample.exchange.util.LocalDateRingBuffer;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service("CurrencyConversionRateContainerStore")
public class ConversionRateContainerStoreImpl implements ConversionRateContainerStore {
    private final AppProperties properties;

    private LocalDateRingBuffer<ConversionRateContainer> buffer;
    private HashSet<String> fromCurrencyCodes = new HashSet<>();
    private HashSet<String> toCurrencyCodes = new HashSet<>();

    @Autowired
    public ConversionRateContainerStoreImpl(AppProperties properties) {
        this.properties = properties;
        int sizeInDays = this.properties.getConversionRateStore().getSizeInDays();
        buffer = new LocalDateRingBuffer<>(sizeInDays, LocalDate.now());
    }

    @Override
    public Optional<BigDecimal> getConversionRate(LocalDate date, String fromCurrencyCode, String toCurrencyCode) {
        Optional<ConversionRateContainer> optionalItem = buffer.getItemAtDate(date);
        Optional<BigDecimal> output = Optional.empty();

        if (optionalItem.isPresent()) {
            output = optionalItem.get().getConversionRate(fromCurrencyCode, toCurrencyCode);
        }

        log.log(Level.TRACE, "date: {}, fromCurrencyCode: {}, toCurrencyCode: {}, output: {}", date,
                fromCurrencyCode, toCurrencyCode, output);

        return output;
    }

    @Override
    public boolean addConversionRateContainer(ConversionRateContainer container, LocalDate date) {
        Optional<Integer> optionalIndex = buffer.canAddOnDate(date);
        boolean output = false;

        if (optionalIndex.isPresent()) {
            fromCurrencyCodes.addAll(container.getFromCurrencyCodes());
            toCurrencyCodes.addAll(container.getToCurrencyCodes());
            container.emptyCurrencyCodeSets();

            buffer.add(optionalIndex.get(), container);

            output = true;
        }

        log.log(Level.TRACE, "container: {}, date: {}, output: {}", container, date, output);

        return output;
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
