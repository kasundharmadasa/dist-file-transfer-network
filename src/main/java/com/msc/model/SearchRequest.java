package com.msc.model;

/**
 * Model class to represent a search request.
 */
public class SearchRequest {

   private String initiatedIp;
   private Integer initiatedPort;
   private String receivedIp;
   private Integer receivedPort;
   private String searchString;
   private Integer hops;

    public SearchRequest(String initiatedIp, Integer initiatedPort, String receivedIp, Integer receivedPort,
                         String searchString, Integer hops) {
        this.initiatedIp = initiatedIp;
        this.initiatedPort = initiatedPort;
        this.receivedIp = receivedIp;
        this.receivedPort = receivedPort;
        this.searchString = searchString;
        this.hops = hops;
    }

    public String getInitiatedIp() {
        return initiatedIp;
    }

    public void setInitiatedIp(String initiatedIp) {
        this.initiatedIp = initiatedIp;
    }

    public Integer getInitiatedPort() {
        return initiatedPort;
    }

    public void setInitiatedPort(Integer initiatedPort) {
        this.initiatedPort = initiatedPort;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getReceivedIp() {
        return receivedIp;
    }

    public void setReceivedIp(String receivedIp) {
        this.receivedIp = receivedIp;
    }

    public Integer getReceivedPort() {
        return receivedPort;
    }

    public void setReceivedPort(Integer receivedPort) {
        this.receivedPort = receivedPort;
    }

    public Integer getHops() {
        return hops;
    }

    public void setHops(Integer hops) {
        this.hops = hops;
    }
}
