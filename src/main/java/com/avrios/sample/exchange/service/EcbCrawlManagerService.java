package com.avrios.sample.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;

import static java.time.temporal.ChronoUnit.DAYS;

@Service("EcbCrawlManagerService")
@RequiredArgsConstructor
@Log
public class EcbCrawlManagerService {
    private final CurrencyConversionRateContainerStoreImpl store;
    private final EcbCurrencyConversionRateClientService ecbClient;
    private final EcbCurrencyConversionRateXmlParserService parser;

    @Scheduled(cron = "${EcbCawlManager.attemptCrawlMissingDatesCron}")
    public void attemptCrawlMissingDates() {
        List<LocalDate> missingDates = store.getMissingDates();

        if (missingDates.size() > 0) {
            Integer dayWindow = ((Long) DAYS.between(store.getHeadDate(), missingDates.get(0))).intValue();

            ecbClient.retrieveXmlFileDayWindow(dayWindow,
                    xml -> parser.process(missingDates, xml, store::addConversionRateContainer),
                    error -> log.log(Level.SEVERE, "Could not retrieve xml file!"));
        }
    }
}
