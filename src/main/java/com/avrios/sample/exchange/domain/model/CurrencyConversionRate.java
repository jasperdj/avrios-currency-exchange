package com.avrios.sample.exchange.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * CurrencyConversionRate is a model class that contains a single-date conversion rate registration.
 */
@Data
@AllArgsConstructor
public class CurrencyConversionRate {
    private String fromCurrencyCode;
    private String toCurrencyCode;
    private LocalDate date;
    private BigDecimal price;
}
