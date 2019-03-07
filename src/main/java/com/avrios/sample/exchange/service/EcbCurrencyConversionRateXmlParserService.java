package com.avrios.sample.exchange.service;

import com.avrios.sample.exchange.domain.model.CurrencyConversionRateContainer;
import lombok.extern.java.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class EcbCurrencyConversionRateXmlParserService {

    public Optional<List<CurrencyConversionRateContainer>> getMissingCurrencyConversionRateContainers(List<LocalDate> missingDates, String xmlString) {
        Optional<Document> xmlDocument = getXmlDocument(xmlString);
        if (xmlDocument.isPresent()) {
            Optional<Node> cubeNode = getCubeNode(xmlDocument.get());

            if (cubeNode.isPresent()) {
                return Optional.of(Collections.EMPTY_LIST);
            }
        }

        return Optional.empty();
    }

    private Optional<Document> getXmlDocument(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlString));
            return Optional.of(builder.parse(is));
        } catch (Exception e) {
            log.log(Level.SEVERE, e.toString());
        }

        return Optional.empty();
    }

    //Todo: add XSD validation
    private Optional<Node> getCubeNode(Document document) {
        NodeList rootchildNodes = document.getDocumentElement().getChildNodes();

        if (rootchildNodes.getLength() == 3) {
            Node cubeContainer = rootchildNodes.item(2);
            if (cubeContainer.getNodeName().equals("Cube") && cubeContainer.hasChildNodes())
                return Optional.of(cubeContainer);
        }

        return Optional.empty();
    }
}
