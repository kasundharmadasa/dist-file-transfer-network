package com.msc.connector;

import com.msc.config.NodeConfig;
import com.msc.handler.IncomingMsgHandler;
import com.msc.parser.MessageParser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The UDP implementation for the connector.
 */
public class UDPConnector implements Connector {

    private static DatagramSocket socket;

    private static volatile UDPConnector connector;

    private UDPConnector() {
        try {
            socket = new DatagramSocket(NodeConfig.getInstance().getUdpPort());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        executorService = Executors.newFixedThreadPool(30);
    }

    public static UDPConnector getInstance() {
        if (connector == null) {
            synchronized (UDPConnector.class) {
                if (connector == null) {
                    connector = new UDPConnector();
                }
            }
        }
        return connector;
    }

    private static ExecutorService executorService;

    @Override
    public void send(String message, InetAddress receiverAddress, int port) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(
                buffer, buffer.length, receiverAddress, port);
        socket.send(packet);
    }

    @Override
    public Future<String> receive() throws IOException {
        byte[] bufferIncoming = new byte[55000];
        DatagramPacket incomingPacket = new DatagramPacket(bufferIncoming, bufferIncoming.length);
        socket.receive(incomingPacket);
        String incomingMessage = new String(bufferIncoming);

        MessageParser messageParser = new MessageParser();
        IncomingMsgHandler responseHandler = messageParser.parse(incomingMessage);

        //System.out.println(incomingMessage + " " + incomingPacket.getAddress().getHostAddress() + ":" + incomingPacket.getUdpPort());
        return (Future<String>) executorService.submit(() -> responseHandler.handle(incomingMessage,
                incomingPacket.getAddress().getHostAddress(), incomingPacket.getPort()));
    }

    public void killExecutorService() {
        executorService.shutdown();
    }
}
