package com.avrios.sample.exchange.util;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.time.temporal.ChronoUnit.DAYS;

@Log4j2
public class LocalDateRingBuffer<N> {
    private List<N> slots;
    private Integer head;
    private Queue<Integer> missingSlots;
    @Getter
    private LocalDate headDate;
    @Getter
    private int size;

    public LocalDateRingBuffer(int size, LocalDate date) {
        this.size = size;
        this.slots = new ArrayList<>(Collections.nCopies(size, null));
        this.head = 0;
        this.headDate = date;

        instantiateMissingSlots(size);
    }

    /**
     * Check if slot at Date is available for data entry
     *
     * @param date given date
     * @return Optional index of the to be added item at Date
     */
    public Optional<Integer> canAddOnDate(LocalDate date) {
        Optional<Integer> index = getIndex(date);

        if (!index.isPresent()) {
            if (headShouldMoveUp(date)) {
                moveHeadUp(DAYS.between(headDate, date));
                return Optional.of(head);
            }

            return Optional.empty();
        }

        return index;
    }

    /**
     * Unchecked item insertion, please call canAddOnDate first!
     *
     * @param index index retrieved with canAddOnDate
     * @param item  to be added item
     */
    public void add(Integer index, N item) {
        slots.set(index, item);
        missingSlots.remove(index);
        log.trace("index: {}, item: {}", index, item);
    }

    /**
     * Get item at date
     *
     * @param date date
     * @return item
     */
    public Optional<N> getItemAtDate(LocalDate date) {
        Optional<Integer> index = getIndex(date);

        return index.map(integer -> slots.get(integer));
    }

    /**
     * Move the head of the item array up to clear up space for a new date entry
     */
    public void moveHeadUp() {
        head = (head + 1) % size;
        headDate = headDate.plusDays(1);
        if (slots.get(head) != null) missingSlots.add(head);
        slots.set(head, null);
        log.trace("head: {}, headDate: {}", head, headDate);
    }

    public void moveHeadUp(long times) {
        for (int i = 0; i < times; i++) moveHeadUp();
    }

    /**
     * Get empty item slot dates
     *
     * @return List of dates
     */
    public List<LocalDate> getEmptyItemSlotDates() {
        return missingSlots.stream()
                .map(this::getDateFromIndex)
                .collect(Collectors.toList());
    }

    private void instantiateMissingSlots(int size) {
        this.missingSlots = new LinkedList<>();
        IntStream.range(1, size).forEach(x -> missingSlots.add(x));
        missingSlots.add(0);
    }

    /**
     * Get index for given date
     *
     * @param date date
     * @return index of item on date
     */
    private Optional<Integer> getIndex(LocalDate date) {
        if (date.isBefore(headDate.minusDays(size - 1)) || date.isAfter(headDate)) return Optional.empty();

        Long daysBetweenDates = DAYS.between(date, headDate);
        if (Math.abs(daysBetweenDates) > size) return Optional.empty();

        int index = head - daysBetweenDates.intValue();
        if (index < 0) index += size;

        return Optional.of(index);
    }

    private LocalDate getDateFromIndex(int index) {
        // Todo: simplify calculation
        int differenceInDaysFromHead = Math.abs(Math.abs(index - head) - (index <= head ? 0 : size));
        return headDate.minusDays(differenceInDaysFromHead);
    }

    private boolean headShouldMoveUp(LocalDate date) {
        return (date.isEqual(LocalDate.now()) || date.isBefore(LocalDate.now())) && headDate.isBefore(date);
    }

}
