package com.msc.connector;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Future;

/**
 * This interface is used to send messages.
 */
public interface Connector {

    void send(String message, InetAddress receiverAddress, int port) throws IOException;

    Future<String> receive() throws IOException;

}
