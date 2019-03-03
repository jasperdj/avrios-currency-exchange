package com.avrios.sample.exchange.service;

import com.avrios.sample.exchange.domain.model.CurrencyConversionRate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service("CurrencyConversionRateStore")
public class CurrencyConversionRateStoreImpl implements CurrencyConversionRateStore {
    @Override
    public Optional<BigDecimal> getConversionRatePrice(LocalDate date, String fromCurrencyCode, String toCurrencyCode) {
        return Optional.of(new BigDecimal("1.1111"));
    }

    @Override
    public void addConversionRate(CurrencyConversionRate currencyConversionRate) {

    }

    @Override
    public void addConversionRates(List<CurrencyConversionRate> currencyConversionRateList) {

    }
}
