package com.avrios.sample.exchange.service;

import com.avrios.sample.exchange.domain.model.CurrencyConversionRateContainer;
import lombok.extern.java.Log;
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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.logging.Level;

@Log
@Service("EcbCurrencyConversionRateXmlParserService")
public class EcbCurrencyConversionRateXmlParserService {

    // Todo: add to properties file
    private final String timeAttributeName = "time";
    private final String currencyAttributeName = "currency";
    private final String rateAttributeName = "rate";
    private final String defaultFromCurrencyCode = "EURO";
    private MessageDigest md;


    public EcbCurrencyConversionRateXmlParserService() {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.log(Level.SEVERE, e.toString());
        }
    }

    public void process(List<LocalDate> missingDates,
                        String xmlString, BiConsumer<CurrencyConversionRateContainer, LocalDate> processor) {
        Optional<Document> xmlDocument = getXmlDocument(xmlString);
        if (xmlDocument.isPresent()) {
            Optional<NodeList> dateNodes = getDateNodes(xmlDocument.get());

            if (dateNodes.isPresent()) {
                processContainers(missingDates, dateNodes.get(), processor);
            }
        }
    }

    private List<CurrencyConversionRateContainer> processContainers(List<LocalDate> missingDates,
                                                                    NodeList dateNodes, BiConsumer<CurrencyConversionRateContainer, LocalDate> processor) {
        List<CurrencyConversionRateContainer> containers = new LinkedList<>();

        int missingDatesIndex = 0;
        for (int i = dateNodes.getLength() - 1; i >= 0 && missingDatesIndex < missingDates.size(); i--) {
            Node dateNode = dateNodes.item(i);
            String date = getAttr(dateNode, timeAttributeName);

            while (!getDate(date).isEqual(missingDates.get(missingDatesIndex)) &&
                    getDate(date).isAfter(missingDates.get(missingDatesIndex))) {
                missingDatesIndex++;
            }

            LocalDate missingDate = missingDates.get(missingDatesIndex);

            if (date.equals(getDateString(missingDate))) {
                processor.accept(extractContainer(dateNode), missingDate);
                missingDatesIndex++;
            }
        }

        return containers;
    }

    private CurrencyConversionRateContainer extractContainer(Node dateNode) {
        String md5Hash = getMd5Hash(dateNode.getTextContent());
        CurrencyConversionRateContainer container = new CurrencyConversionRateContainer(md5Hash);
        NodeList currencyConversionNodes = dateNode.getChildNodes();

        extractRates(container, currencyConversionNodes);

        return container;
    }

    private void extractRates(CurrencyConversionRateContainer container, NodeList currencyConversionNodes) {
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

    // Todo: create util class
    private String getMd5Hash(String string) {
        byte[] digest = md.digest(string.getBytes(StandardCharsets.UTF_8));

        return new String(digest, StandardCharsets.UTF_8);
    }

    private String getDateString(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    // Todo: create util class
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

    //Todo: add XSD validation
    private Optional<NodeList> getDateNodes(Document document) {
        Element documentElement = document.getDocumentElement();
        NodeList rootchildNodes = documentElement.getChildNodes();

        if (rootchildNodes.getLength() == 3) {
            Node cubeContainer = rootchildNodes.item(2);
            if (cubeContainer.getNodeName().equals("Cube") && cubeContainer.hasChildNodes())
                return Optional.of(cubeContainer.getChildNodes());
        }

        return Optional.empty();
    }
}
