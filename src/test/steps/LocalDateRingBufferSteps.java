package steps;

import com.avrios.sample.exchange.util.LocalDateRingBuffer;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;
import org.powermock.reflect.Whitebox;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

public class LocalDateRingBufferSteps {
    private LocalDateRingBuffer<Integer> ringBuffer;
    private List<Boolean> addAttemptSuccess;
    private List<Optional<Integer>> retrievalResults;

    @Given("a ring buffer with size {int} is created with Date day: {int}  month: {int} year: {int}")
    public void aRingBufferWithSizeIsCreatedWithDateDayMonthYear(int arg0, int arg1, int arg2, int arg3) {
        ringBuffer = new LocalDateRingBuffer<>(arg0, LocalDate.of(arg3, arg2, arg1));
        addAttemptSuccess = new ArrayList<>();
        retrievalResults = new ArrayList<>();
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
        List<Integer> slots = getSlots();

        for (int i = 0; i < dataTable.width(); i++) {
            int index = Integer.valueOf(dataTable.column(i).get(0));
            String valueString = dataTable.column(i).get(1);

            assertThat(slots.get(index), valueString.equals("null") ?
                    is(nullValue()) :
                    equalTo(Integer.valueOf(valueString)));
        }

    }

    @And("all items in the ring buffer have the same value as their slot index")
    public void allItemsInTheRingBufferHaveTheSameValueAsTheirSlotIndex() {
        List<Integer> slots = getSlots();

        for (int i = 0; i < slots.size(); i++) {
            slots.set(i, i);
        }

        Whitebox.setInternalState(ringBuffer, "missingSlots", new LinkedList<>());

        setSlots(slots);
    }

    @And("head is {int} with headDate day: {int}  month: {int} year: {int}")
    public void headIsWithHeadDateDayMonthYear(int arg0, int arg1, int arg2, int arg3) {
        Whitebox.setInternalState(ringBuffer, "head", arg0);
        Whitebox.setInternalState(ringBuffer, "headDate", LocalDate.of(arg3, arg2, arg1));

    }

    private List<Integer> getSlots() {
        return new ArrayList<>(Whitebox.getInternalState(ringBuffer, "slots"));
    }

    private void setSlots(List<Integer> slots) {
        Whitebox.setInternalState(ringBuffer, "slots", slots);
    }

    @When("items are retrieved with the following dates")
    public void itemsAreRetrievedWithTheFollowingDates(DataTable dataTable) {
        List<String> row = dataTable.row(0);

        for (String item : row) {
            String[] dateComponents = item.split("-");
            LocalDate date = LocalDate.of(
                    Integer.valueOf(dateComponents[2]),
                    Integer.valueOf(dateComponents[1]),
                    Integer.valueOf(dateComponents[0])
            );

            retrievalResults.add(ringBuffer.getItemAtDate(date));
        }
    }

    @Then("the retrieval results are")
    public void theRetrievalResultsAre(DataTable dataTable) {
        for (int i = 0; i < dataTable.row(0).size(); i++) {
            String value = dataTable.row(0).get(i);
            if (value.equals("null")) {
                assertThat(retrievalResults.get(i).isPresent(), equalTo(false));
            } else {
                assertThat(retrievalResults.get(i).isPresent(), equalTo(true));
                assertThat(retrievalResults.get(i).get(), equalTo(Integer.valueOf(value)));
            }
        }
    }

    @When("Head moves up {int} times")
    public void headMovesUpTimes(int arg0) {
        for (int i = 0; i < arg0; i++) {
            ringBuffer.moveHeadUp();
        }
    }

    @Then("head is {int}")
    public void headIs(int arg0) {
        assertThat(Whitebox.getInternalState(ringBuffer, "head"), equalTo(arg0));
    }

    @And("headDate is day: {int} month: {int} year: {int}")
    public void headdateIsDayMonthYear(int arg0, int arg1, int arg2) {
        assertThat(Whitebox.getInternalState(ringBuffer, "headDate"),
                equalTo(LocalDate.of(arg2, arg1, arg0)));

    }

    @And("the following item dates are missing")
    public void theFollowingItemDatesAreMissing(DataTable dataTable) {
        List<LocalDate> expectedDates = dataTable.row(0).stream()
                .map(x -> x.split("-"))
                .map(x -> LocalDate.of(Integer.valueOf(x[2]), Integer.valueOf(x[1]), Integer.valueOf(x[0])))
                .collect(Collectors.toList());

        assertThat(ringBuffer.getEmptyItemSlotDatesUpToDate(), equalTo(expectedDates));
    }

    @Then("no item dates are missing")
    public void noItemDatesAreMissing() {
        assertThat(ringBuffer.getEmptyItemSlotDatesUpToDate(), equalTo(Collections.EMPTY_LIST));
    }
}
