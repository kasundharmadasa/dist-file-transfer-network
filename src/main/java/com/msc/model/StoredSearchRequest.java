package com.msc.model;

/**
 * Model class to represent a stored search request.
 */
public class StoredSearchRequest {

   private SearchRequest searchRequest;
   private Long storedTime;

    public StoredSearchRequest(SearchRequest searchRequest, Long storedTime) {
        this.searchRequest = searchRequest;
        this.storedTime = storedTime;
    }

    public SearchRequest getSearchRequest() {
        return searchRequest;
    }

    public void setSearchRequest(SearchRequest searchRequest) {
        this.searchRequest = searchRequest;
    }

    public Long getStoredTime() {
        return storedTime;
    }

    public void setStoredTime(Long storedTime) {
        this.storedTime = storedTime;
    }
}
