package com.avrios.sample.exchange.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("avrios.ecb")
@Data
public class EcbProperties {

    private Client client;
    private Manager manager;
    private Parser parser;

    @Data
    public static class Client {
        private int connectionTimeout = 5000;
        private int maxRetries = 5;
    }

    @Data
    public static class Manager {
        private String crawlCron = "* * */1 * * *";
    }

    @Data
    public static class Parser {
        private String timeAttributeName = "time";
        private String currencyAttributeName = "currency";
        private String rateAttributeName = "rate";
        private String defaultFromCurrencyCode = "EURO";
    }
}
