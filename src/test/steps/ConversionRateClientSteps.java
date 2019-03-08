package steps;

import com.avrios.sample.exchange.service.Ecb.EcbConversionRateClientService;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ConversionRateClientSteps {
    private String xmlString;
    private EcbConversionRateClientService client = new EcbConversionRateClientService();
    private boolean failure = false;

    @When("xml string is requested for a {int} day window")
    public void xmlStringIsRequestedForADayWindow(int arg0) {
        client.retrieveXmlFileDayWindow(arg0, x -> xmlString = x, x -> failure = true);
    }

    @Then("xml string for {int} day window is returned")
    public void xmlStringForDayWindowIsReturned(int arg0) {
        String lastDateInDayWindow = LocalDate.now().minusDays(arg0)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String secondLastDateInDayWindow = LocalDate.now().minusDays(arg0 - 1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        assertThat(xmlString.contains(lastDateInDayWindow) || xmlString.contains(secondLastDateInDayWindow)
                , equalTo(true));
    }
}
