package com.msc.handler;

import com.msc.model.Neighbours;
import com.msc.model.Node;

import java.util.StringTokenizer;

/**
 * This class is used to handle graceful leave messages received from the neighbours.
 */
public class LeaveRequestHandler implements IncomingMsgHandler {

    @Override
    public void handle(String message) {
        Neighbours neighbourTable = Neighbours.getInstance();
        System.out.println("Leave request: " + message);
        StringTokenizer stringTokenizer = new StringTokenizer(message, " ");
        int length = Integer.parseInt(stringTokenizer.nextToken());
        String command = stringTokenizer.nextToken();

        String ip = stringTokenizer.nextToken();
        String port = stringTokenizer.nextToken();

        System.out.println("Received leave message: " + ip + ":" + port);

        // Remove the neighbour details from the neighbour table.
        neighbourTable.remove(new Node(ip, Integer.parseInt(port)));
        System.out.println("Removed neighbour: " + ip + ":" + port);
    }
}
