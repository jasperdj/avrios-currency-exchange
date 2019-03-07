package com.avrios.sample.exchange.domain.model;

import lombok.Data;

@Data
public class EcbCurrencyConversionRateClientStream {
    private int days;
    private String url;
}
