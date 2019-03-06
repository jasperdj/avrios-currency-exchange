package com.avrios.sample.exchange.util;

import lombok.Getter;
import lombok.extern.java.Log;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
        this.missingSlots = new LinkedList<>();
        this.headDate = date;
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

        boolean dateIsOlderThanHeadDate = date.compareTo(headDate) > 0;

        if (!slotIsAvailable(index.get())) {
            if (dateIsOlderThanHeadDate) {
                log.log(Level.WARNING, "no slot is available for date {0}", date);
                return Optional.empty();
            } else {
                log.log(Level.WARNING, "slot {0} will be overwritten", index.get());
            }
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
        head = head + 1 % size;
        headDate = headDate.plusDays(1);
        slots.set(head, null);
        missingSlots.add(head);
    }

    /**
     * Get empty item slot dates, up until a given date
     *
     * @param date limiting date
     * @return List of dates
     */
    public List<LocalDate> getEmptyItemSlotDatesUpToDate(LocalDate date) {
        return missingSlots.stream()
                .map(this::getDateFromIndex)
                .filter(indexDate -> indexDate.compareTo(headDate) <= 0)
                .collect(Collectors.toList());
    }

    /**
     * Get index for given date
     *
     * @param date date
     * @return index of item on date
     */
    private Optional<Integer> getIndex(LocalDate date) {
        Long daysBetweenDates = DAYS.between(date, headDate);
        if (Math.abs(daysBetweenDates) > size) return Optional.empty();

        int index = head - daysBetweenDates.intValue();
        if (index < 0) index += size;

        return Optional.of(index);
    }

    private LocalDate getDateFromIndex(int index) {
        int differenceInDaysFromHead = Math.abs(index - head) - (index < head ? 0 : size);
        return headDate.minusDays(differenceInDaysFromHead);
    }

    private boolean slotIsAvailable(int index) {
        return missingSlots.contains(index);
    }

}
