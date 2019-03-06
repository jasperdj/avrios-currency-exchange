# Datastore design document

## 1. Input ECB
**1.1 Streams**
- 1.1.1 90 days https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml
- 1.1.2 yesterday http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml
- 1.1.3 future proofing: expect the possibility for new data streams to be added (I.E. 365 days)

**1.2 Structure of stream**
- 1.2.1 Root `<Cube>`
- 1.2.2 Root child `<Cube time:String>`
- 1.2.3 Root child child `<Cube currency:String rate:BigDecimal/>`

**1.3 Data consistency/quality**
- 1.3.1 It is historical data and therefore provided data should be final.
- 1.3.2 Since this API could potentially be used for large (automated) currency conversions, therefore, it may be desirable to include configuration options for extra data consistency checks with ECB.
- 1.3.3 It is not certain at what time new conversion rates are added.
- 1.3.4 Amount of currencies may change in the future.

**1.4 Service availability**
- 1.4.1 It is given that the service should be considered unstable, this could mean that a) service down-time should be expected, and b) as `1.3.2` suggests data may not be consistent and therefore subject to change.

## 2. Output exchange
**2.1 API calls**
- 2.1.1 BigDecimal API/retrieveEuroConversionRate(toCurrency:String, date: LocalDate)
- 2.1.2 Possible future api call: BigDecimal API/retrieveConversionRate(fromCurrency:String, toCurrency:String, date: LocalDate)

**2.2 validation**
- 2.2.1 Validate if toCurrency exists

## 3. ECB Crawl manager
**3.1 Crawl events**
- 3.1.1 on interval: data consistency check: during this event all available data will be fetched and data hashes will be compared to look for data inconsistencies.
- 3.1.2 on interval: check and fill missing data with the appropriate stream `1.4.1`

**3.2 Crawling strategies**
- 3.2.1 data consistency check
```
in largest stream (1.1.1) {
    for XML root child (1.2.2) {
        if (XML root child hash != data store hash) {
            logWarning();
            fixDataStoreItem(); 
        }
    }
}
```

- 3.2.2 check and fill missing data
```
xml = null
for date in dataStore.missingDataQueue {
  Optional<Integer> index = dataStore.canAddOnDate(date);
  if (index.isPresent()) {
     if (xml == null && getSmallestStreamThatHasMoreThan(date.now()-date) ) {
        xml = fetchStreamXML
        if (streamCannotBeReached) break;
     }
     
     if (xml != null) {
        if (!XmlContainsDate(date)) continue;
        dataStore.add(index.get(), xml.find(date))
     }
     
  }
  
  dataStore.missingDataQueue.pop(date);
}
```

## 4. Data store
**4.1 Requirements**
- 4.1.1 To comply to `2.1.1` and `2.1.2` able to retrieve BigDecimal conversion price based on date, fromCurrency and toCurrency.
- 4.1.2 Fixed size(90 days) of amount of currency conversion days.
- 4.1.3 Do not allow duplicate currency conversion rates to exist.
- 4.1.4 Allow for data store updates when inconsistent data is detected
- 4.1.5 to comply with 3.1.2 can return missing values.


**4.2 Future proofing**
- 4.2.1 Allow for fixed size of days (`3.1.2`) to be configured smaller or larger than 90.
- 4.2.2 Allow for the possibility of persistent storage in the future through abstraction.

**4.3 Time/space complexity parameters**
- 4.3.1 DS: Amount of dates stored (90 days) `1.2.2` 
- 4.3.2 DR: Amount of dates provided by stream (90 days) `1.1.1` 
- 4.3.3 FC: Amount of fromCurrencyCodes (to support `2.1.2`)
- 4.3.4 TC: Amount of toCurrencyCodes
- 4.3.5 CD: Amount of CurrencyConversionRates per date
- 4.3.6 MS: Amount of missing slots

**4.4 public functions and time complexity**
- 4.4.1 O(1): Add CurrencyConversionRateContainer
- 4.4.2 0(1): get a Currency Conversion Rate with parameters `date`, `fromCurrencyCode` and `toCurrencyCode`
- 4.4.3 0(1): validate if given `fromCurrencyCode` or `toCurrencyCode` has been used in data store for `2.2.1`
- 4.4.4 O(1): free up a date slot for `5.1.1`
- 4.4.5 0(1): queue: get unfilled dates for `3.2.2`

**4.5 implementation**
4.5.1 LocalDate index based Ring buffer with DS slots
4.5.1.1 Instantiated structure
```
LocalDateRingBuffer<N> {
  CurrencyConversionRateContainer[] slots = new CurrencyConversionRateContainer[size];
  Integer head = 0;
  Queue<Integer> missingSlots = new LinkedList(); 
  LocalDate headDate;
}
```
4.5.1.2 getIndex
```
Optional<Integer> getIndex(LocalDate) {
  if (headDate == null) return Optional.empty();
  
  Integer daysBetweenDates = DAYS.between(headDate, date);
  if (Math.abs(daysBetween) > size) return Optional.empty();
  
  Integer index = head - daysBetweenDates;
  if (index < 0) index += size;
  
  return index;
  
}
```
4.5.1.3 slotIsAvailable(Integer) 
```
boolean slotIsAvailable(int index) {
  return missingSlots.contains(index);
}

```
The big O complexity here is O(WS), it's still better than O(DS), since the potential amount of missingslots is much lower than DS. 
I'm curious to find out whether there is a better approach. 

4.5.1.4 canAddOnDate
```
Optional<Integer> canAddOnDate(LocalDate date) {
  Optional<Integer> index = getIndex(date);
    
  if (!index.isPresent()) return false;
    
  boolean dateIsOlderThanHeadDate = date.compareTo(headDate) > 0;
    
  if (!slotIsAvailable(index.get())) {
    if (dateIsOlderThanHeadDate) {
      log.warn("no slot is available for date X and data Y");
      return Optional.empty();
    } else {
      log.warn("slot X will be ovewritten with Y");
    }
  }
    
  return index;
}
```


4.5.1.5 Add 
```
void add(Integer index, CurrencyConversionRateContainer currencyConversionRateContainer) {
  slot[index] = currencyConversionRateContainer;
  
  if (!missingSlots.remove(index)) log.error("Added item to index that was not found in missing slots!")
  
  return true;
}
```

4.5.1.6 GetCurrencyConversionRateContainer
```
Optional<CurrencyConversionRateContainer> getCurrencyConversionRateContainer(LocalDate date) {
  Optional<Integer> index = getIndex(date);
  
  if (index.isPresent()) {
    return Optional.ofNullable(slots[index.get()]);
  }
  
  return Optional.empty();
}
```

4.5.1.7 moveHeadUp
```
void moveHeadUp() {
  head = head + 1 % size;
  headDate = headDate.plusDays(1);
  slots[head] = null;
  missingSlots.add(head);
}
```

4.5.1.8 getMissingValuesUpToDate
```
List<LocalDate> getMissingValueDatesUpToDate(LocalDate date) {
  return missingSlots.stream()
    .map(this::getDateFromIndex)
    .takeWhile(date -> date.compareTo(headDate) <= 0)
    .collect(Collectors.toList());
}

private LocalDate getDateFromIndex(int index) {
    int differenceInDaysFromHead = Math.abs(index-head) - (index < head ? 0 : size)
    return headDate.minusDays(differenceInDaysFromHead);
}
```

**4.5.2 Test Scenarios**
- DS is larger than stream can provide
- DS is smaller than stream provides


## 5. Data store item invalidator. 
**5.1 functionality**
5.1.1 interval: The data store item invalidator calls datastore.moveHeadUp() to invalidate the oldest date.

## 6. Model

**6.1 CurrencyConversionRateContainer**

Note: I picked a combined key hashmap instead of nested hashmap. Why? 
Pros: More human readable, this was a priority for this project.
Cons: If in the future currency combinations are uncertain, performance will suffer because keys are iterated through brute force key generation.

*NOTICE*: I've Isolated these hashset collection in CurrencyConversionRateContainerStore to comply to single responsibility.
- 6.2.1 Static final String CURRENCY_DELIMITER = "_";
- 6.2.2 Static final HashSet<String> TO_CURRENCY_CODES = new HashSet<>(); // incl. getter
- 6.2.3 Static final HashSet<String> FROM_CURRENCY_CODES = new HashSet<>(); // incl. getter
Why this hashSet, this hashset will be used for REST input validation. 

- 6.2.4 String xmlHash;
- 6.2.5 HashMap<String, BigDecimal> currencyConversionRates
- 6.2.6 getConversionRate
```
Optional<BigDecimal> getConversionRate(String fromCurrencyCode, String toCurrencyCode) {
  String key = fromCurrencyCode + CURRENCY_DELIMITER + toCurrencyCode;
  
  return Optional.ofNullable(currencyConversionRates.get(key));
}
```
- 6.2.7 addConversionRate
```
void addConversionRate(String fromCurrencyCode, String toCurrencyCode, BigDecimal price) {
  if (fromCurrencyCode.size() > 0 && toCurrencyCode.size() > 0 && price.compareTo(new BigDecimal("0")) > 0) {
    currencyConversionRates.put(fromCurrencyCode + CURRENCY_DELIMITER + toCurrencyCode, price);
  }
}
```

## Classes

CurrencyConversionRateContainerStore: 
 getConversionRate
 |-> LocalDateRingBuffer