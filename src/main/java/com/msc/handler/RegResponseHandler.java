package com.msc.handler;

import com.msc.Controller;
import com.msc.model.Neighbours;
import com.msc.model.Node;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * This class is used to handle the register response messages from the bootstrap server.
 */
public class RegResponseHandler implements IncomingMsgHandler {

    @Override
    public void handle(String message) {

        Neighbours neighbourTable = Neighbours.getInstance();
        System.out.println("Reg response: " + message);
        StringTokenizer stringTokenizer = new StringTokenizer(message, " ");
        int length = Integer.parseInt(stringTokenizer.nextToken());
        String command = stringTokenizer.nextToken();
        int nodeCount = Integer.parseInt(stringTokenizer.nextToken());
        switch (nodeCount) {
            case 9996:
                System.out.println(" failed, can’t register. BS is full.");
                break;
            case 9997:
                System.out.println(" failed, registered to another user, try a different IP and port");
                break;
            case 9998:
                System.out.println(" failed, already registered to you, unregister first");
                break;
            case 9999:
                System.out.println(" failed, there is some error in the command");
                break;
            case 0:
                System.out.println(" Request is successful but, no other nodes in the system");
                break;
            default:
                for (int i = 0; i < nodeCount; i++) {
                    String ip = stringTokenizer.nextToken().trim();
                    String port = stringTokenizer.nextToken().trim();

                    // Add the node information sent by the bootstrap server into the neighbour table.
                    neighbourTable.insert(new Node(ip, Integer.parseInt(port)));

                    try {

                        // Send join message to the added neighbours.
                        System.out.println("Send join message: " + ip + ":" + port);
                        Controller.join(ip, Integer.parseInt(port));
                        System.out.println("Joined: " + ip + ":" + port);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

}
