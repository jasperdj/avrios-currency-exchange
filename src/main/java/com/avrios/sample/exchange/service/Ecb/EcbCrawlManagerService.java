package com.avrios.sample.exchange.service.Ecb;

import com.avrios.sample.exchange.service.ConversionRateContainerStoreImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Service("EcbCrawlManagerService")
@RequiredArgsConstructor
@Log4j2
public class EcbCrawlManagerService {
    private final ConversionRateContainerStoreImpl store;
    private final EcbConversionRateClientService ecbClient;
    private final EcbConversionRateXmlParserService parser;

    @Scheduled(cron = "${ecb.manager.crawl-cron}")
    public void attemptCrawlMissingDates() {
        log.log(Level.INFO, "Attempted crawl of missing dates");

        List<LocalDate> missingDates = store.getMissingDates();

        if (missingDates.size() > 0) {
            Integer dayWindow = ((Long) DAYS.between(missingDates.get(0), store.getHeadDate())).intValue();

            ecbClient.retrieveXmlFileDayWindow(dayWindow,
                    xml -> parser.process(missingDates, xml.replaceAll("[\\r\\n]", ""),
                            store::addConversionRateContainer),
                    error -> log.log(Level.ERROR, "Could not retrieve xml file!"));
        }
    }
}
