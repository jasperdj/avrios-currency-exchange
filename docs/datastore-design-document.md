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

## 3. Crawl manager
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
  if (missingItemStillFitsInDataStore -> date > date.now()-DS(4.3.1)) {
     if (xml == null && getSmallestStreamThatHasMoreThan(date.now()-date) ) {
        xml = fetchStreamXML
        if (streamCannotBeReached) break;
     }
     
     if (xml != null) {
        if (!XmlContainsDate(date)) continue;
        dataStore.add(xml.find(date))
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
- 

**4.3 Time/space complexity parameters**
- 4.3.1 DS: Amount of dates stored (90 days) `1.2.2` 
- 4.3.2 DR: Amount of dates provided by stream (90 days) `1.1.1` 
- 4.3.3 FC: Amount of fromCurrencyCodes (to support `2.1.2`)
- 4.3.4 TC: Amount of toCurrencyCodes

**4.4 public functions and time complexity**
- 4.4.1 O(n): Add List<CurrencyConversionRate>
- 4.4.2 0(FC*TC): get CurrencyConversionRate.price from `date`, `fromCurrencyCode` and `toCurrencyCode`
- 4.4.3 0(1): validate if given `fromCurrencyCode` or `toCurrencyCode` has been used in data store for `2.2.1`
- 4.4.4 O(1) free up a date slot for `5.1.1`
- 4.4.5 queue: get unfilled dates for `3.2.2`

****

## 5. Data store item invalidator. 
**5.1 functionality**
5.1.1 interval: The data store item invalidator invalidates last value in the data store, to free up space for a new value

## 6. Model
**6.1 CurrencyConversionRate**
- 6.1.1 String fromCurrencyCode;
- 6.1.2 String toCurrencyCode;
- 6.1.3 BigDecimal price;

**6.2 CurrencyConversionRateContainer**
- 6.2.1 String xmlHash;
- 6.2.2 List<CurrencyConversionRate>