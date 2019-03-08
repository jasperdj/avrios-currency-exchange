package com.avrios.sample.exchange.service.Ecb;

import com.avrios.sample.exchange.configuration.EcbProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.asynchttpclient.Dsl.config;

@Service("EcbCurrencyConversionRateClientService")
@Log
public class EcbConversionRateClientService {
    private final EcbProperties ecbProperties;

    private List<EcbConversionRateStream> streams;
    private AsyncHttpClient asyncHttpClient;

    @Value("${service.EcbConversionRateClientService.connectionTimeout}")
    private Integer connectionTimeout = 5000;
    @Value("${service.EcbConversionRateClientService.maxRetries}")
    private Integer maxRetries = 5;

    @Autowired
    public EcbConversionRateClientService(EcbProperties ecbProperties) {
        this.ecbProperties = ecbProperties;

        // Todo: implement SSL certificates
        asyncHttpClient = asyncHttpClient(config()
                .setConnectTimeout(this.ecbProperties.getClient().getConnectionTimeout())
                .setMaxRequestRetry(this.ecbProperties.getClient().getMaxRetries())
                .setAcceptAnyCertificate(true));

        // Todo: make this configurable from application.properties
        streams = new ArrayList<>();
        streams.add(new EcbConversionRateStream(90,
                "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml"));
        streams.add(new EcbConversionRateStream(1,
                "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml"));
    }

    /**
     * Retrieve Xml file by Day window
     *
     * @param amountOfDays amount of days in the past that you seek to fetch for.
     * @param success      function parameter to process the result
     * @param failure      result function
     */
    public void retrieveXmlFileDayWindow(Integer amountOfDays, Consumer<String> success, Consumer<String> failure) {
        EcbConversionRateStream stream = getSmallestStreamForDayWindow(amountOfDays);

        asyncHttpClient
                .prepareGet(stream.getUrl())
                .execute()
                .toCompletableFuture()
                .exceptionally(t -> {
                    failure.accept(t.toString());
                    return null;
                })
                .thenApply(Response::getResponseBody)
                .thenAccept(success)
                .join();
    }

    private EcbConversionRateStream getSmallestStreamForDayWindow(Integer amountOfDays) {
        // Todo: solve ugly nesting
        return streams.stream()
                .filter(x -> x.getDays() >= amountOfDays)
                .min(Comparator.comparing(EcbConversionRateStream::getDays))
                .orElse(
                        streams.stream()
                                .max(Comparator.comparing(EcbConversionRateStream::getDays))
                                .get()
                );
    }

}

@Data
@AllArgsConstructor
class EcbConversionRateStream {
    private int days;
    private String url;
}
