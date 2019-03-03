package com.avrios.sample.exchange.service;

import java.util.HashMap;

// Todo: make this store configurable from application.properties.
// Task 1. load initial state from application.properties
// Task 2. Validate if state loaded from application.properties includes every necessary key-value pair.

/**
 * CrawlManagerStoreServices centralizes and isolates the manager store's state.
 */
public class CrawlManagerStoreService {
    private HashMap<String, Object> datastore;


    public CrawlManagerStoreService() {
        datastore = new HashMap<>();
    }

    public void insert(String key, Object value) {
        datastore.put(key, value);
    }

    public Object getObject(String key) {
        return datastore.get(key);
    }

    public String getString(String key) {
        return String.valueOf(datastore.get(key));
    }

    // Todo: checked casting with parameters key and class

}
