package com.avrios.sample.exchange.service;

import com.avrios.sample.exchange.domain.model.CurrencyConversionRateContainer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service("CurrencyConversionRateStore")
public class CurrencyConversionRateContainerStoreImpl implements CurrencyConversionRateContainerStore {

    @Override
    public Optional<BigDecimal> getConversionRatePrice(LocalDate date, String fromCurrencyCode, String toCurrencyCode) {
        return Optional.empty();
    }

    @Override
    public boolean addConversionRateContainer(CurrencyConversionRateContainer container) {
        return false;
    }

    @Override
    public List<String> getFromCurrencyCodes() {
        return null;
    }

    @Override
    public List<String> getToCurrencyCodes() {
        return null;
    }
}
