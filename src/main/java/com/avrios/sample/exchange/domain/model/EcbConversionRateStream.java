package com.avrios.sample.exchange.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EcbConversionRateStream {
    private int days;
    private String url;
}
