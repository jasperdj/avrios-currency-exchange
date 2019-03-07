package steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;

// Todo: complete test methods
public class CurrencyConversionRateContainerStoreSteps {
    @Given("currency conversion store contains the following entries")
    public void currencyConversionStoreContainsTheFollowingEntries(DataTable dataTable) {

    }


    @Then("the price {string} is returned")
    public void thePriceIsReturned(String price) {
    }

    @When("currency rate is requested from {string} to {string} on {string}")
    public void currencyRateIsRequestedFromToOn(String fromCurrency, String toCurrency, String date) {

    }

    @Then("an empty optional is returned")
    public void anEmptyOptionalIsReturned() {

    }

    @When("currency conversion rate is added with the following properties")
    public void currencyConversionRateIsAddedWithTheFollowingProperties(DataTable dataTable) {

    }

    @And("the amount of entries in the data store is {int}")
    public void theAmountOfEntriesInTheDataStoreIs(int arg0) {
    }

    @When("currency conversion rates are added with the following properties")
    public void currencyConversionRatesAreAddedWithTheFollowingProperties(DataTable dataTable) {

    }

    @And("a warn log with text {string} has been pushed to the console")
    public void aWarnLogWithTextHasBeenPushedToTheConsole(String log) {

    }
}
