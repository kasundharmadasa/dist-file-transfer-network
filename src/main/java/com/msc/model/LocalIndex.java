package com.msc.model;

import java.util.List;

public class LocalIndex implements Comparable<LocalIndex> {

    private String ip;
    private Integer port;
    private List<List<String>> files;
    private Integer hops;

    public LocalIndex(String ip, Integer port, List<List<String>> files, Integer hops) {
        this.ip = ip;
        this.port = port;
        this.files = files;
        this.hops = hops;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public List<List<String>> getFiles() {
        return files;
    }

    public void setFiles(List<List<String>> files) {
        this.files = files;
    }

    public Integer getHops() {
        return hops;
    }

    public void setHops(Integer hops) {
        this.hops = hops;
    }

    @Override
    public int compareTo(LocalIndex localIndex) {
        return this.getHops().compareTo(localIndex.getHops());
    }
}
