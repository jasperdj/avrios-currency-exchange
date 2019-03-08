package com.avrios.sample.exchange.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("avrios.conversionRateStore")
@Data
public class ConversionRateStoreProperties {
    private int sizeInDays = 90;
}
