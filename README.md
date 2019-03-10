# Avrios Exchange project
Here you'll find my implementation of the exercise, to showcase some of my experience I've included
- Data structures & algorithms: LocalDateRingBuffer
- Consumer/BiConsumer implementation: EcbConversionRateXmlParserService
- Reactive HTTP client / CompleteableFuture: EcbConversionRateClientService
- BDD / unit testing / PowerMock / Mockito: See Test folder, you can run tests by executing *CucumberTest*
- GitFlow: due to limitations of Github projects I used TaskBranches and labels each branch with Epic/Feature/Task.
- Java 8 Streams

## How does Data flow in this application? 
1. EcbCrawlManagerServices launches a cronjob to start crawlingEcb.
2. EcbConversionRateClientService will fetch the XML file
3. EcbConversionRateXmlParser will process the XML file
4. ConversionRateContainerStoreImpl will provides storage functionality on top of LocalDateRingBuffer for context specific operations.

## How do I start the application?
With maven
`mvn spring-boot:run` (port 8080)

Or with docker
`docker-compose up -d` (port 80)

The API call to retrieve a currency conversion rate is a GET request on url:
`http://localhost:8080/api/euro-currency-conversion/2019-03-08/USD/`

Additionally, it is also possible to specify the currency you want to convert from, although only data for euro is available.
`http://localhost:8080/api/currency-conversion/2019-03-08/EURO/USD/`


## What are note worthy features?
1. Storage time complexity: All operations on the LocalDateRingBuffer/ConversionRateContainerStore have O(1) complexity.
2. Storage space efficiency: To minimize memory footprint dates associated to conversion rates are not stored, but inferred.
3. Minimizing external rest requests: EcbConversionRateClient picks the smallest necessary API stream (1 day, 90 day, etc) based on day slots are still missing.
4. Efficient xml parsing: XmlParserService will only parse missing day slots.

## What would you do if you would spend more time on this project?
1. Efficient polling (now it's simply a cronjob)
  - Automaticly determining the update timeframe of the external service.
2. Cloud deployment configuration
3. SSL certificates
4. Cronjob: Data consistency check.
5. End to end test by mocking the external API

Feel free to leave a code review :)