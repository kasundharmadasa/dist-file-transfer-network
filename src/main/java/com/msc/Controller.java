package com.msc;

import com.msc.config.NodeConfig;
import com.msc.util.MessageUtil;
import com.msc.connector.Connector;
import com.msc.connector.UDPConnector;
import com.msc.model.MessageConstants;

import java.io.IOException;
import java.net.InetAddress;

public class Controller {

    private static Connector connector = UDPConnector.getInstance();

    public static void register(String nodeIP, Integer nodePort, String nodeUsername) throws IOException {

        String message = MessageUtil.generateMessage(MessageConstants.REG_MESSAGE, nodeIP, nodePort, nodeUsername);
        connector.send(message, InetAddress.getByName("localhost"), 55555);
    }

    public static void unregister() throws IOException {

        String message = MessageUtil.generateMessage(MessageConstants.UNREG_MESSAGE, NodeConfig.getInstance().getIp(),
                NodeConfig.getInstance().getPort());
        connector.send(message, InetAddress.getByName("localhost"), 55555);
    }

    public static void join(String ip, int port) throws IOException {

        String message = MessageUtil.generateMessage(MessageConstants.JOIN_MESSAGE, NodeConfig.getInstance().getIp(),
                NodeConfig.getInstance().getPort());
        connector.send(message, InetAddress.getByName(ip), port);

    }

    public static void leave(String ip, int port) throws IOException {

        String message = MessageUtil.generateMessage(MessageConstants.LEAVE_MESSAGE, NodeConfig.getInstance().getIp(),
                NodeConfig.getInstance().getPort());
        connector.send(message, InetAddress.getByName(ip), port);

    }

    public static void search(String ip, int port) throws IOException {

        String message = MessageUtil.generateMessage(MessageConstants.SEARCH_MESSAGE, NodeConfig.getInstance().getIp(),
                NodeConfig.getInstance().getPort());
        connector.send(message, InetAddress.getByName(ip), port);

    }

}
