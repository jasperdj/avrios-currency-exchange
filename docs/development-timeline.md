# Development timeline
**Why this document?** The goal of this document is to take you(the reviewer) with me along my journey towards completing this project to give insight on _how I work_. 

**How will this document achieve that goal?** By sharing insights, problems, considered options and solutions that occured with every Feature/Task (and its commits) in chronological order.

**How are features/tasks specified?**
For this project Github projects are used to organize epics/feature/tasks, because of Github limitations I use the following title formatting.

    ID [Epic - feature] Tasknr. task (R:requirement|S:stretch goal)

## The beginning
- Task 11: create a key value store service
  - **Why?** The idea was to create an isolated key-value store to store the state of the crawling process, this way in the future this store could be pluggable with an external datastore for improved crash resiliency.
  - **Why cancelled?** Due to Java's strong typing causing extra layers of casting complexity and its small scope(only one class would use it) this unit was cancelled for the sake of prevention of over-engineering.
- Task 15: create 'currencyConversionRate' datastore interface and a mock.
  - Future proofing: It is very possible that a new feature may include retrieving a currency conversion rate from an other currency than EURO, to future proof such feature the property `fromCurrencyCode` has been included in the model class CurrencyConversionRate.
  - Question for later: how much impact does the extra property have on the performance of the data store? Is it worth it?
- 