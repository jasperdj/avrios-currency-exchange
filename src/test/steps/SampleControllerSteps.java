package steps;

import com.avrios.sample.exchange.controller.SampleController;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class SampleControllerSteps {
    String outputHelloWorldRequest;

    @When("hello world is requested")
    public void helloWorldIsRetrieved() {
        outputHelloWorldRequest = new SampleController().helloWorld();
    }

    @Then("the text {string} is returned.")
    public void theTextHelloWorldIsReturned(String string) {
        assertThat(outputHelloWorldRequest, equalTo(string));
    }

}
