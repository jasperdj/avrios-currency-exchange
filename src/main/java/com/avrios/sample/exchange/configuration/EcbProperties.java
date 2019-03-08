package com.avrios.sample.exchange.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ecb")
@Data
public class EcbProperties {

    private Client client = new Client();
    private Manager manager = new Manager();
    private Parser parser = new Parser();

    @Data
    public class Client {
        private int connectionTimeout = 5000;
        private int maxRetries = 5;
    }

    @Data
    public class Manager {
        private String crawlCron = "* * */1 * * *";
    }

    @Data
    public class Parser {
        private String timeAttributeName = "time";
        private String currencyAttributeName = "currency";
        private String rateAttributeName = "rate";
        private String defaultFromCurrencyCode = "EURO";
    }
}
