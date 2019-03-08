package steps;

import com.avrios.sample.exchange.domain.model.CurrencyConversionRateContainer;
import com.avrios.sample.exchange.service.EcbCurrencyConversionRateXmlParserService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.IsEqual.equalTo;

public class EcbCurrencyConversionRateXmlParserSteps {
    private EcbCurrencyConversionRateXmlParserService parser = new EcbCurrencyConversionRateXmlParserService();
    private String validXml = "<gesmes:Envelope xmlns:gesmes=\"http://www.gesmes.org/xml/2002-08-01\" xmlns=\"http://www.ecb.int/vocabulary/2002-08-01/eurofxref\"><gesmes:subject>Reference rates</gesmes:subject><gesmes:Sender><gesmes:name>European Central Bank</gesmes:name></gesmes:Sender><Cube><Cube time=\"2019-03-07\"><Cube currency=\"USD\" rate=\"1.1271\"/><Cube currency=\"JPY\" rate=\"125.97\"/><Cube currency=\"BGN\" rate=\"1.9558\"/><Cube currency=\"CZK\" rate=\"25.61\"/><Cube currency=\"DKK\" rate=\"7.461\"/><Cube currency=\"GBP\" rate=\"0.8588\"/><Cube currency=\"HUF\" rate=\"315.36\"/><Cube currency=\"PLN\" rate=\"4.2991\"/><Cube currency=\"RON\" rate=\"4.7415\"/><Cube currency=\"SEK\" rate=\"10.5625\"/><Cube currency=\"CHF\" rate=\"1.1355\"/><Cube currency=\"ISK\" rate=\"136.8\"/><Cube currency=\"NOK\" rate=\"9.786\"/><Cube currency=\"HRK\" rate=\"7.4158\"/><Cube currency=\"RUB\" rate=\"74.3115\"/><Cube currency=\"TRY\" rate=\"6.1171\"/><Cube currency=\"AUD\" rate=\"1.6014\"/><Cube currency=\"BRL\" rate=\"4.3234\"/><Cube currency=\"CAD\" rate=\"1.5131\"/><Cube currency=\"CNY\" rate=\"7.5622\"/><Cube currency=\"HKD\" rate=\"8.8476\"/><Cube currency=\"IDR\" rate=\"15990.17\"/><Cube currency=\"ILS\" rate=\"4.0782\"/><Cube currency=\"INR\" rate=\"78.884\"/><Cube currency=\"KRW\" rate=\"1273.15\"/><Cube currency=\"MXN\" rate=\"21.8244\"/><Cube currency=\"MYR\" rate=\"4.6065\"/><Cube currency=\"NZD\" rate=\"1.6631\"/><Cube currency=\"PHP\" rate=\"58.923\"/><Cube currency=\"SGD\" rate=\"1.5308\"/><Cube currency=\"THB\" rate=\"35.819\"/><Cube currency=\"ZAR\" rate=\"16.1514\"/></Cube><Cube time=\"2019-03-06\"><Cube currency=\"USD\" rate=\"1.1305\"/><Cube currency=\"JPY\" rate=\"126.4\"/><Cube currency=\"BGN\" rate=\"1.9558\"/><Cube currency=\"CZK\" rate=\"25.592\"/><Cube currency=\"DKK\" rate=\"7.4609\"/><Cube currency=\"GBP\" rate=\"0.8597\"/><Cube currency=\"HUF\" rate=\"315.51\"/><Cube currency=\"PLN\" rate=\"4.2996\"/><Cube currency=\"RON\" rate=\"4.7463\"/><Cube currency=\"SEK\" rate=\"10.5375\"/><Cube currency=\"CHF\" rate=\"1.136\"/><Cube currency=\"ISK\" rate=\"136.8\"/><Cube currency=\"NOK\" rate=\"9.804\"/><Cube currency=\"HRK\" rate=\"7.421\"/><Cube currency=\"RUB\" rate=\"74.4208\"/><Cube currency=\"TRY\" rate=\"6.1247\"/><Cube currency=\"AUD\" rate=\"1.6072\"/><Cube currency=\"BRL\" rate=\"4.2682\"/><Cube currency=\"CAD\" rate=\"1.5128\"/><Cube currency=\"CNY\" rate=\"7.5857\"/><Cube currency=\"HKD\" rate=\"8.8743\"/><Cube currency=\"IDR\" rate=\"15999.4\"/><Cube currency=\"ILS\" rate=\"4.0875\"/><Cube currency=\"INR\" rate=\"79.432\"/><Cube currency=\"KRW\" rate=\"1275.05\"/><Cube currency=\"MXN\" rate=\"21.7999\"/><Cube currency=\"MYR\" rate=\"4.6289\"/><Cube currency=\"NZD\" rate=\"1.6679\"/><Cube currency=\"PHP\" rate=\"59.083\"/><Cube currency=\"SGD\" rate=\"1.5349\"/><Cube currency=\"THB\" rate=\"36.012\"/><Cube currency=\"ZAR\" rate=\"16.0583\"/></Cube></Cube></gesmes:Envelope>";
    private String invalidXml = "<gesmes:Envelope xmlns:gesmes=\"http://www.gesmes.org/xml/2002-08-01\" xmlns=\"http://www.ecb.int/vocabulary/2002-08-01/eurofxref\"><gesmes:subject>Reference rates</gesmes:subject><gesmes:Sender><gesmes:name>European Central Bank</gesmes:name></gesmes:Sender><Cube></Cube></gesmes:Envelope>";
    private final String xmlStart = "<gesmes:Envelope xmlns:gesmes=\"http://www.gesmes.org/xml/2002-08-01\" xmlns=\"http://www.ecb.int/vocabulary/2002-08-01/eurofxref\"><gesmes:subject>Reference rates</gesmes:subject><gesmes:Sender><gesmes:name>European Central Bank</gesmes:name></gesmes:Sender><Cube>";
    private final String xmlEnd = "</Cube></gesmes:Envelope>";
    private String xml;

    private List<LocalDate> missingDates = Arrays.asList(
            LocalDate.of(2019, 3, 7),
            LocalDate.of(2019, 3, 6));

    private List<CurrencyConversionRateContainer> containersResults = new ArrayList<>();
    private List<LocalDate> containerDateResults = new ArrayList<>();

    @When("an invalid xml is given to be parsed")
    public void anInvalidXmlIsGivenToBeParsed() {
        parser.process(missingDates, invalidXml, this::processContainers);
    }

    @Then("no containers are processed")
    public void aEmptyOptionalIsReturned() {
        assertThat(containersResults.size(), equalTo(0));
    }

    @When("an valid xml is given to be parsed")
    public void anValidXmlIsGivenToBeParsed() {
        parser.process(missingDates, validXml, this::processContainers);

    }

    @Then("a list is returned")
    public void aListIsReturned() {
        assertThat(containersResults.size(), greaterThan(0));
    }

    private void processContainers(CurrencyConversionRateContainer container, LocalDate date) {
        containersResults.add(container);
        containerDateResults.add(date);
    }

    @Given("a valid xml with the following values")
    public void aValidXmlWithTheFollowingValues(DataTable dataTable) {
        xml = xmlStart;

        xmlAddDate(0, dataTable.column(0).get(0));
        for (int i = 1; i < dataTable.height(); i++) {
            List<String> row = dataTable.row(i);
            if (row.get(0).trim().length() > 0) {
                xmlAddDate(2, row.get(0));
            } else {
                xml += "<Cube currency=\"" + row.get(1) + "\" rate=\"" + row.get(2) + "\"/>";
            }
        }

        xmlAddDate(1, "");
        xml += xmlEnd;
    }

    private void xmlAddDate(int mode, String date) {
        if (mode == 1 || mode == 2) xml += "</Cube>";
        if (mode == 0 || mode == 2) xml += "<Cube time=\"" + date + "\">";
    }

    @And("the following missing values")
    public void theFollowingMissingValues(DataTable dataTable) {
        missingDates = new ArrayList<>();

        dataTable.row(0).forEach(x -> missingDates.add(getDate(x)));
    }

    private LocalDate getDate(String string) {
        String[] components = string.split("-");
        return LocalDate.of(
                Integer.valueOf(components[0]),
                Integer.valueOf(components[1]),
                Integer.valueOf(components[2])
        );
    }

    @When("the xml is parsed")
    public void theXmlIsParsed() {
        parser.process(missingDates, xml, this::processContainers);
    }

    @Then("the following containers are processed")
    public void theFollowingContainersAreProcessed(DataTable dataTable) {
        int index = -1;

        for (int i = 0; i < dataTable.height(); i++) {
            List<String> row = dataTable.row(i);
            if (row.get(0).trim().length() > 0) {
                index++;
                assertThat(index, lessThan(containersResults.size()));
                assertThat(containerDateResults.get(index), equalTo(getDate(row.get(0))));
            } else {
                HashMap<String, BigDecimal> rates = containersResults.get(index).getCurrencyConversionRates();
                assertThat(rates.get("EURO_" + row.get(1)), equalTo(new BigDecimal(row.get(2))));
            }
        }
    }

    @Given("an empty xml")
    public void anEmptyXml() {
        xml = xmlStart + xmlEnd;
    }
}
