package com.avrios.sample.exchange.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties("app")
public class AppProperties {
    private ConversionRateStore conversionRateStore = new ConversionRateStore();

    @Data
    public static class ConversionRateStore {
        private int sizeInDays = 90;
    }
}
