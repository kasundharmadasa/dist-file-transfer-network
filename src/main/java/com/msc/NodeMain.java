package com.msc;

import com.msc.config.NodeConfig;
import com.msc.connector.Connector;
import com.msc.connector.UDPConnector;
import com.msc.model.LocalIndex;
import com.msc.model.LocalIndexTable;
import com.msc.model.Neighbours;
import com.msc.model.Node;
import com.msc.model.SearchRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Future;

/**
 * This is the main class for the node.
 */
public class NodeMain {

    public static void main(String args[]) {

        // Add the node configurations
        NodeConfig.getInstance().setIp(args[0]);
        NodeConfig.getInstance().setPort(Integer.parseInt(args[1]));
        NodeConfig.getInstance().setUsername(args[2]);
        NodeConfig.getInstance().setBootstrapServerIp(args[3]);
        NodeConfig.getInstance().setBootstrapServerPort(Integer.parseInt(args[4]));
        NodeConfig.getInstance().setSearchCacheEnabled(Boolean.parseBoolean(args[5]));
        Connector connector = UDPConnector.getInstance();

        // Select files to be hosted by the server.
        List<List<String>> selectedFiles = selectFilesForNode();

        System.out.println("Listing on port " + NodeConfig.getInstance().getPort() + " ....");

        // Listen for incoming messages.
        new Thread(() -> {
            while (true) {
                Future<String> stringFuture = null;
                try {
                    stringFuture = connector.receive();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ).start();

        // Register in the Bootstrap Server
        try {
            Controller.register(NodeConfig.getInstance().getIp(), NodeConfig.getInstance().getPort(),
                    NodeConfig.getInstance().getUsername());
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            if ("exit".equals(command)) {
                try {
                    System.out.println("Sending unregister message");
                    Controller.unregister();
                    List<Node> neighbours = Neighbours.getInstance().getPeerNodeList();
                    for (Node node : neighbours) {
                        System.out.println("Sending leave message: " + node.getNodeIp() + ":" + node.getPort());
                        Controller.leave(node.getNodeIp(), node.getPort());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }

            if (command.startsWith("search")) {
                String searchString = command.substring(command.indexOf(" ") + 1);

                SearchRequest searchRequest = new SearchRequest(NodeConfig.getInstance().getIp(),
                        NodeConfig.getInstance().getPort(),
                        NodeConfig.getInstance().getIp(),
                        NodeConfig.getInstance().getPort(), searchString, 1);

                try {
                    Controller.search(searchRequest);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * Select files to be hosted by the server.
     *
     * @return
     */
    private static List<List<String>> selectFilesForNode() {
        List<String> initialFileList = Arrays.asList("Adventures of Tintin",
                "Jack and Jill",
                "Glee",
                "The Vampire Diarie",
                "King Arthur",
                "Windows XP",
                "Harry Potter",
                "Kung Fu Panda",
                "Lady Gaga",
                "Twilight",
                "Windows 8",
                "Mission Impossible",
                "Turn Up The Music",
                "Super Mario",
                "American Pickers",
                "Microsoft Office 2010",
                "Happy Feet",
                "Modern Family",
                "American Idol",
                "Hacking for Dummies");

        List<Integer> selectedFileIndexes = new ArrayList<>();
        while (selectedFileIndexes.size() < 4) {
            Integer randIndex = new Random().nextInt(initialFileList.size());
            if (!selectedFileIndexes.contains(randIndex)) {
                selectedFileIndexes.add(randIndex);
            }
        }

        List<List<String>> selectedFiles = new ArrayList<>();
        for (Integer selectedFileIndex : selectedFileIndexes) {
            System.out.println("Selected file: " + initialFileList.get(selectedFileIndex));
            String selectedFile = initialFileList.get(selectedFileIndex);
            selectedFiles.add(Arrays.asList(selectedFile.split(" ")));
        }

        LocalIndexTable.getInstance().insert(new LocalIndex(NodeConfig.getInstance().getIp(),
                NodeConfig.getInstance().getPort(), selectedFiles, 0));

        return selectedFiles;
    }

    //simple function to echo data to terminal
    public static void echo(String msg) {
        System.out.println(msg);
    }
}
