package com.avrios.sample.exchange.util;

import lombok.Getter;
import lombok.extern.java.Log;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.time.temporal.ChronoUnit.DAYS;

@Log
public class LocalDateRingBuffer<N> {
    private List<N> slots;
    private Integer head;
    private Queue<Integer> missingSlots;
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

        if (!index.isPresent()) return Optional.empty();

        if (!missingSlots.contains(index.get()))
            log.log(Level.WARNING, "slot {0} will be overwritten", index.get());

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

        if (!missingSlots.remove(index))
            log.log(Level.WARNING, "index {0} was not found in missing slots!", index);
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
    }

    /**
     * Get empty item slot dates
     *
     * @return List of dates
     */
    public List<LocalDate> getEmptyItemSlotDatesUpToDate() {
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

}
