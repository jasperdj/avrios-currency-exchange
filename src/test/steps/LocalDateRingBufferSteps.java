package steps;

import com.avrios.sample.exchange.util.LocalDateRingBuffer;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;
import org.powermock.reflect.Whitebox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

public class LocalDateRingBufferSteps {
    LocalDateRingBuffer<Integer> ringBuffer;
    List<Boolean> addAttemptSuccess = new ArrayList<>();

    @Given("a ring buffer with size {int} is created with Date day: {int}  month: {int} year: {int}")
    public void aRingBufferWithSizeIsCreatedWithDateDayMonthYear(int arg0, int arg1, int arg2, int arg3) {
        ringBuffer = new LocalDateRingBuffer<>(arg0, LocalDate.of(arg3, arg2, arg1));
    }

    @When("attempt to add item with Integer {int} and Date day: {int}  month: {int} year: {int}")
    public void attemptToAddItemWithIntegerAndDateDayMonthYear(int arg0, int arg1, int arg2, int arg3) {
        Optional<Integer> index = ringBuffer.canAddOnDate(LocalDate.of(arg3, arg2, arg1));

        if (index.isPresent()) {
            ringBuffer.add(index.get(), arg0);
            addAttemptSuccess.add(true);
        } else {
            addAttemptSuccess.add(false);
        }
    }

    @Then("ring buffer contains the following items at given indexes")
    public void ringBufferContains(DataTable dataTable) {
        List<Integer> slots = Whitebox.getInternalState(ringBuffer, "slots");

        for (int i = 0; i < dataTable.width(); i++) {
            int index = Integer.valueOf(dataTable.column(i).get(0));
            String valueString = dataTable.column(i).get(1);

            assertThat(slots.get(index), valueString.equals("null") ?
                    is(nullValue()) :
                    equalTo(Integer.valueOf(valueString)));
        }

    }
}
