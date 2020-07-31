package com.msc.model;

import java.util.List;

public class SearchResponse {

    public Long receivedTimestamp;
    public Integer hops;
    public List<String> receivedFileNames;

    public SearchResponse(Long receivedTimestamp, int hops, List<String> receivedFileNames) {
        this.receivedTimestamp = receivedTimestamp;
        this.hops = hops;
        this.receivedFileNames = receivedFileNames;
    }

    public Long getReceivedTimestamp() {
        return receivedTimestamp;
    }

    public void setReceivedTimestamp(Long receivedTimestamp) {
        this.receivedTimestamp = receivedTimestamp;
    }

    public Integer getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }

    public List<String> getReceivedFileNames() {
        return receivedFileNames;
    }

    public void setReceivedFileNames(List<String> receivedFileNames) {
        this.receivedFileNames = receivedFileNames;
    }
}
