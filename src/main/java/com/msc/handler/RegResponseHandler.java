package com.msc.handler;

import com.msc.Controller;
import com.msc.config.NodeConfig;
import com.msc.model.CommonConstants;
import com.msc.model.Neighbours;
import com.msc.model.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * This class is used to handle the register response messages from the bootstrap server.
 */
public class RegResponseHandler implements IncomingMsgHandler {

    @Override
    public void handle(String message, String sourceIp, Integer sourcePort) {

        Neighbours neighbourTable = Neighbours.getInstance();
        StringTokenizer stringTokenizer = new StringTokenizer(message, " ");
        int length = Integer.parseInt(stringTokenizer.nextToken());
        String command = stringTokenizer.nextToken();
        int nodeCount = Integer.parseInt(stringTokenizer.nextToken().replaceAll("[^\\d.]", ""));
        switch (nodeCount) {
            case 9996:
                System.out.println("Failed, can’t register. BS is full.");
                break;
            case 9997:
                System.out.println("Failed, registered to another user, try a different IP and port");
                break;
            case 9998:
                System.out.println("Failed, already registered to you, unregister first");

                // Unregister and re register.
                try {
                    Controller.unregister();

                    Thread.sleep(1000);
                    Controller.register(NodeConfig.getInstance().getIp(), NodeConfig.getInstance().getPort(),
                            NodeConfig.getInstance().getUsername());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case 9999:
                System.out.println("Failed, there is some error in the command");
                break;
            case 0:
                System.out.println("Request is successful, no nodes in the system");
                break;
            default:
                List<Node> neighbours = new ArrayList<>();
                List<Node> selectedNeighbours = new ArrayList<>();

                for (int i = 0; i < nodeCount; i++) {
                    String ip = stringTokenizer.nextToken().trim();
                    String port = stringTokenizer.nextToken().trim();

                    neighbours.add(new Node(ip, Integer.parseInt(port)));
                }

                if (neighbours.size() > CommonConstants.MAX_ALLOWED_TO_JOIN) {
                    List<Integer> randNodeIndexes = new ArrayList<>();
                    while (randNodeIndexes.size() < CommonConstants.MAX_ALLOWED_TO_JOIN) {
                        Integer randIndex = new Random().nextInt(neighbours.size());
                        if (!randNodeIndexes.contains(randIndex)) {
                            selectedNeighbours.add(neighbours.get(randIndex));
                            randNodeIndexes.add(randIndex);
                        }
                    }

                } else {
                    selectedNeighbours.addAll(neighbours);
                }

                // Join with the selected neighbours
                for (Node selectedNeighbour : selectedNeighbours) {
                    try {
                        // Add the node information sent by the bootstrap server into the neighbour table.
                        neighbourTable.insert(selectedNeighbour);

                        // Send join message to the added neighbours.
                        System.out.println("Send join message: " + selectedNeighbour.getNodeIp() + ":" +
                                selectedNeighbour.getPort());
                        Controller.join(selectedNeighbour.getNodeIp(), selectedNeighbour.getPort());
                        System.out.println("Joined: " + selectedNeighbour.getNodeIp() + ":" +
                                selectedNeighbour.getPort());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

}
