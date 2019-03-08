package steps;

import com.avrios.sample.exchange.configuration.AppProperties;
import com.avrios.sample.exchange.domain.model.ConversionRateContainer;
import com.avrios.sample.exchange.service.ConversionRateContainerStoreImpl;
import com.avrios.sample.exchange.util.LocalDateRingBuffer;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Todo: complete test methods
public class ConversionRateContainerStoreSteps {
    private AppProperties properties = new AppProperties();
    private ConversionRateContainerStoreImpl store = new ConversionRateContainerStoreImpl(properties);
    private Optional<BigDecimal> returnedPrice;

    @Given("currency conversion store contains the following entries")
    public void currencyConversionStoreContainsTheFollowingEntries(DataTable dataTable) {
        LocalDateRingBuffer<ConversionRateContainer> buffer = mock(LocalDateRingBuffer.class);
        when(buffer.canAddOnDate(anyObject())).thenReturn(Optional.of(1));

        for (int row = 1; row < dataTable.height(); row++) {
            List<String> columns = dataTable.row(row);
            LocalDate date = stringToDate(columns.get(2));
            ConversionRateContainer container = new ConversionRateContainer();
            container.addConversionRate(columns.get(0), columns.get(1), new BigDecimal(columns.get(3)));

            //todo: fix default value overriding
            when(buffer.getItemAtDate(LocalDate.of(2019, 2, 2))).thenReturn(Optional.empty());
            when(buffer.getItemAtDate(date)).thenReturn(Optional.of(container));
        }

        Whitebox.setInternalState(store, "buffer", buffer);
    }


    @Then("the price {string} is returned")
    public void thePriceIsReturned(String price) {
        assertThat(returnedPrice.get(), equalTo(new BigDecimal(price)));
    }

    @When("currency rate is requested from {string} to {string} on {string}")
    public void currencyRateIsRequestedFromToOn(String fromCurrency, String toCurrency, String date) {
        returnedPrice = store.getConversionRate(stringToDate(date), fromCurrency, toCurrency);
    }

    @Then("an empty optional is returned")
    public void anEmptyOptionalIsReturned() {
        assertThat(returnedPrice.isPresent(), equalTo(false));
    }

    @When("currency conversion rate is added with the following properties")
    public void currencyConversionRateIsAddedWithTheFollowingProperties(DataTable dataTable) {
        List<String> row = dataTable.row(1);
        LocalDate date = stringToDate(row.get(2));
        ConversionRateContainer container = new ConversionRateContainer();
        container.addConversionRate(row.get(0), row.get(1), new BigDecimal(row.get(3)));

        store.addConversionRateContainer(container, date);
    }


    private LocalDate stringToDate(String stringDate) {
        String[] dateComponents = stringDate.split("-");

        return LocalDate.of(
                Integer.valueOf(dateComponents[2]),
                Integer.valueOf(dateComponents[1]),
                Integer.valueOf(dateComponents[0]));
    }

    @Then("from currencies codes contain")
    public void fromCurrenciesCodesContain(DataTable dataTable) {
        for (int i = 0; i < dataTable.width(); i++) {
            String currency = dataTable.row(0).get(i);
            assertThat(store.getFromCurrencyCodes().contains(currency), equalTo(true));
        }
    }

    @Then("to currencies codes contain")
    public void toCurrenciesCodesContain(DataTable dataTable) {
        for (int i = 0; i < dataTable.width(); i++) {
            String currency = dataTable.row(0).get(i);
            assertThat(store.getToCurrencyCodes().contains(currency), equalTo(true));
        }
    }
}
