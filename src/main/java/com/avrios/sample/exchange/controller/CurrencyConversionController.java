package com.avrios.sample.exchange.controller;

import com.avrios.sample.exchange.service.CurrencyConversionRateContainerStore;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class CurrencyConversionController {
    private final CurrencyConversionRateContainerStore store;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @ResponseBody
    @RequestMapping("/euro-currency-conversion/{date}/{toCurrencyCode}/")
    public ResponseEntity<BigDecimal> euroCurrencyConversion(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @PathVariable("date") LocalDate date,
            @PathVariable("toCurrencyCode") String toCurrencyCode) {

        return currencyConversion(date, "EURO", toCurrencyCode);
    }

    @ResponseBody
    @RequestMapping("/currency-conversion/{date}/{fromCurrencyCode}/{toCurrencyCode}/")
    public ResponseEntity<BigDecimal> currencyConversion(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @PathVariable("date") LocalDate date,
            @PathVariable("fromCurrencyCode") String fromCurrencyCode,
            @PathVariable("toCurrencyCode") String toCurrencyCode) {

        if (!store.getFromCurrencyCodes().contains(fromCurrencyCode) ||
                !store.getToCurrencyCodes().contains(toCurrencyCode)) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }

        Optional<BigDecimal> rate = store.getConversionRate(date, fromCurrencyCode, toCurrencyCode);

        if (rate.isPresent())
            return new ResponseEntity<>(rate.get(), HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }


}
