package com.msc.handler;

import com.msc.model.Neighbours;
import com.msc.model.Node;

import java.util.StringTokenizer;

/**
 * This class is used to handle the incoming join requests by peer nodes.
 */
public class JoinRequestHandler implements IncomingMsgHandler {

    @Override
    public void handle(String message) {

        Neighbours neighbourTable = Neighbours.getInstance();
        System.out.println("Join request: " + message);
        StringTokenizer stringTokenizer = new StringTokenizer(message, " ");
        int length = Integer.parseInt(stringTokenizer.nextToken());
        String command = stringTokenizer.nextToken();

        String ip = stringTokenizer.nextToken();
        String port = stringTokenizer.nextToken();

        System.out.println("Received join message: " + ip + ":" + port);

        // insert the neighbour details into the neighbour table.
        neighbourTable.insert(new Node(ip, Integer.parseInt(port)));
        System.out.println("Added as a neighbour: " + ip + ":" + port);

    }
}
