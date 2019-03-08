package com.avrios.sample.exchange.configuration;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        @Getter(AccessLevel.NONE)
        @Setter(AccessLevel.NONE)
        private Map<String, String> stream1day = new HashMap<String, String>() {{
            put("dayWindow", "1");
            put("url", "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
        }};

        @Getter(AccessLevel.NONE)
        @Setter(AccessLevel.NONE)
        private Map<String, String> stream90day = new HashMap<String, String>() {{
            put("dayWindow", "90");
            put("url", "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml");
        }};

        private List<Map<String, String>> streams = Arrays.asList(
                stream1day, stream90day
        );
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
