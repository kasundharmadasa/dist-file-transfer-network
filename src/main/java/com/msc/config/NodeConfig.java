package com.msc.config;

/**
 * This class is used to store the configurations of the node.
 */
public class NodeConfig {

    String ip;
    Integer port;
    String username;

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

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
