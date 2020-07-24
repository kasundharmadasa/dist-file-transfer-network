package com.msc.config;

/**
 * This class is used to store the configurations of the node.
 */
public class NodeConfig {

    String ip;
    Integer udpPort;
    Integer tcpPort;
    String username;
    String bootstrapServerIp;
    Integer bootstrapServerPort;
    boolean isSearchCacheEnabled;

    private static volatile NodeConfig nodeConfig;

    private NodeConfig() {

    }

    public static NodeConfig getInstance() {
        if (nodeConfig == null) {
            synchronized (NodeConfig.class) {
                if (nodeConfig == null) {
                    nodeConfig = new NodeConfig();
                }
            }
        }
        return nodeConfig;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(Integer udpPort) {
        this.udpPort = udpPort;
    }

    public Integer getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(Integer tcpPort) {
        this.tcpPort = tcpPort;
    }

    public static NodeConfig getNodeConfig() {
        return nodeConfig;
    }

    public static void setNodeConfig(NodeConfig nodeConfig) {
        NodeConfig.nodeConfig = nodeConfig;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBootstrapServerIp() {
        return bootstrapServerIp;
    }

    public void setBootstrapServerIp(String bootstrapServerIp) {
        this.bootstrapServerIp = bootstrapServerIp;
    }

    public Integer getBootstrapServerPort() {
        return bootstrapServerPort;
    }

    public void setBootstrapServerPort(Integer bootstrapServerPort) {
        this.bootstrapServerPort = bootstrapServerPort;
    }

    public boolean isSearchCacheEnabled() {
        return isSearchCacheEnabled;
    }

    public void setSearchCacheEnabled(boolean searchCacheEnabled) {
        isSearchCacheEnabled = searchCacheEnabled;
    }
}
