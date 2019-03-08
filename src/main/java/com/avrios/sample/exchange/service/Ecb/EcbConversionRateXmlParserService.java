package com.avrios.sample.exchange.service.Ecb;

import com.avrios.sample.exchange.domain.model.ConversionRateContainer;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.logging.Level;

@Log
@Service("EcbCurrencyConversionRateXmlParserService")
public class EcbConversionRateXmlParserService {
    @Value("${service.EcbConversionRateXmlParserService.timeAttributeName}")
    private final String timeAttributeName = "time";
    @Value("${service.EcbConversionRateXmlParserService.currencyAttributeName}")
    private final String currencyAttributeName = "currency";
    @Value("${service.EcbConversionRateXmlParserService.rateAttributeName}")
    private final String rateAttributeName = "rate";
    @Value("${service.EcbConversionRateXmlParserService.defaultFromCurrencyCode}")
    private final String defaultFromCurrencyCode = "EURO";

    /**
     * Process xml
     *
     * @param missingDates the missing dates it will be seeking to fill
     * @param xmlString    xml string
     * @param processor    function parameter to process/send the ConversionRateContainers to its destination
     */
    public void process(List<LocalDate> missingDates,
                        String xmlString, BiConsumer<ConversionRateContainer, LocalDate> processor) {
        Optional<Document> xmlDocument = getXmlDocument(xmlString);
        if (xmlDocument.isPresent()) {
            Optional<NodeList> dateNodes = getDateNodes(xmlDocument.get());

            if (dateNodes.isPresent()) {
                processContainers(missingDates, dateNodes.get(), processor);
            }
        }
    }

    private List<ConversionRateContainer> processContainers(List<LocalDate> missingDates,
                                                            NodeList dateNodes, BiConsumer<ConversionRateContainer, LocalDate> processor) {
        List<ConversionRateContainer> containers = new LinkedList<>();

        int missingDatesIndex = 0;
        for (int i = dateNodes.getLength() - 1; i >= 0; i--) {
            Node dateNode = dateNodes.item(i);
            String date = getAttr(dateNode, timeAttributeName);

            while (thereAreMissingDatesLeft(missingDates, missingDatesIndex) &&
                    dateHasPassedMissingDate(missingDates, missingDatesIndex, date)) {
                missingDatesIndex++;
            }

            LocalDate missingDate = missingDates.get(missingDatesIndex);

            if (dateIsEqualToMissingDate(date, missingDate) || noMissingDatesLeft(missingDates, missingDatesIndex)) {
                processor.accept(extractContainer(dateNode), getDate(date));
            }
        }

        return containers;
    }

    private ConversionRateContainer extractContainer(Node dateNode) {
        ConversionRateContainer container = new ConversionRateContainer();
        NodeList currencyConversionNodes = dateNode.getChildNodes();

        extractRates(container, currencyConversionNodes);

        return container;
    }

    private void extractRates(ConversionRateContainer container, NodeList currencyConversionNodes) {
        for (int i = 0; i < currencyConversionNodes.getLength(); i++) {
            Node currencyConversionNode = currencyConversionNodes.item(i);
            String toCurrencyCode = getAttr(currencyConversionNode, currencyAttributeName);
            BigDecimal rate = new BigDecimal(getAttr(currencyConversionNode, rateAttributeName));

            container.addConversionRate(defaultFromCurrencyCode, toCurrencyCode, rate);
        }
    }

    private String getAttr(Node node, String string) {
        return node.getAttributes().getNamedItem(string).getNodeValue();
    }

    private String getDateString(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private LocalDate getDate(String string) {
        String[] components = string.split("-");

        return LocalDate.of(
                Integer.valueOf(components[0]),
                Integer.valueOf(components[1]),
                Integer.valueOf(components[2])
        );
    }

    private Optional<Document> getXmlDocument(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlString));
            Document document = builder.parse(is);
            return Optional.of(document);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.toString());
        }

        return Optional.empty();
    }

    private Optional<NodeList> getDateNodes(Document document) {
        Element documentElement = document.getDocumentElement();
        NodeList rootchildNodes = documentElement.getChildNodes();

        if (rootchildNodes.getLength() == 3) {
            Node dateContainer = rootchildNodes.item(2);
            if (xmlIsValid(dateContainer))
                return Optional.of(dateContainer.getChildNodes());
        }

        return Optional.empty();
    }

    // Syntactic sugar
    private boolean dateHasPassedMissingDate(List<LocalDate> missingDates, int missingDatesIndex, String date) {
        return !getDate(date).isEqual(missingDates.get(missingDatesIndex)) &&
                getDate(date).isAfter(missingDates.get(missingDatesIndex));
    }

    private boolean thereAreMissingDatesLeft(List<LocalDate> missingDates, int missingDatesIndex) {
        return missingDatesIndex < missingDates.size() - 1;
    }

    private boolean dateIsEqualToMissingDate(String date, LocalDate missingDate) {
        return date.equals(getDateString(missingDate));
    }

    private boolean noMissingDatesLeft(List<LocalDate> missingDates, int missingDatesIndex) {
        return missingDatesIndex == missingDates.size() - 1;
    }

    private boolean xmlIsValid(Node cubeContainer) {
        return cubeContainer.getNodeName().equals("Cube") && cubeContainer.hasChildNodes();
    }
}
