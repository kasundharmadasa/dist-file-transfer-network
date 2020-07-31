package com.msc.model;

public class Search {

    public Long initiatedTimestamp;
    public String searchString;

    public Search(Long initiatedTimestamp, String searchString) {
        this.initiatedTimestamp = initiatedTimestamp;
        this.searchString = searchString;
    }

    public Long getInitiatedTimestamp() {
        return initiatedTimestamp;
    }

    public void setInitiatedTimestamp(Long initiatedTimestamp) {
        this.initiatedTimestamp = initiatedTimestamp;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
