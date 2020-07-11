package com.msc.util;

import com.msc.model.SearchRequest;

/**
 * This is a utility class to hold the common functions.
 */
public class CommonUtil {

    /**
     * Generate search request key from search request
     *
     * @param searchRequest
     * @return
     */
    public static String generateSearchRequestKey(SearchRequest searchRequest) {
        return searchRequest.getInitiatedIp()
                + ":" + searchRequest.getInitiatedPort() + ":" + searchRequest.getSearchString();
    }
}
