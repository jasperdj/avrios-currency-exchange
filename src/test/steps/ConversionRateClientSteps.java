package steps;

import com.avrios.sample.exchange.configuration.EcbProperties;
import com.avrios.sample.exchange.service.Ecb.EcbConversionRateClientService;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.util.StringUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ConversionRateClientSteps {
    private String xmlString;
    private EcbProperties ecbProperties = new EcbProperties();
    private EcbConversionRateClientService client = new EcbConversionRateClientService(ecbProperties);
    private boolean failure = false;

    @When("xml string is requested for a {int} day window")
    public void xmlStringIsRequestedForADayWindow(int arg0) {
        client.retrieveXmlFileDayWindow(arg0, x -> xmlString = x, x -> failure = true);
    }

    @Then("the xml string contains between {int} and {int} day values")
    public void xmlStringForDayWindowIsReturned(int arg0, int arg1) {
        int count = StringUtils.countOccurrencesOf(xmlString, "<Cube time=");
        assertThat(count >= arg0 && count <= arg1, equalTo(true));
    }
}
