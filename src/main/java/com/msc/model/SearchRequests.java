package com.msc.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is used to store the search requests received by the node.
 */
public class SearchRequests {

    private static Map<String, SearchRequest> searchRequestMap;
    private static volatile SearchRequests searchRequests;

    static {
        searchRequestMap = new ConcurrentHashMap<>();
    }

    private SearchRequests() {

    }

    public static SearchRequests getInstance() {
        if (searchRequests == null) {
            synchronized (SearchRequests.class) {
                if (searchRequests == null) {
                    searchRequests = new SearchRequests();
                }
            }
        }
        return searchRequests;
    }

    public void insert(String key, SearchRequest searchRequest) {

        searchRequestMap.put(key, searchRequest);
    }

    public Map<String, SearchRequest> getSearchRequestMap() {
        return searchRequestMap;
    }
}
