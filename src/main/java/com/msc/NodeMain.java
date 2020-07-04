package com.msc;

import com.msc.config.NodeConfig;
import com.msc.connector.Connector;
import com.msc.connector.UDPConnector;
import com.msc.model.Files;
import com.msc.model.Neighbours;
import com.msc.model.Node;

import java.io.IOException;
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

        NodeConfig.getInstance().setIp(args[0]);
        NodeConfig.getInstance().setPort(Integer.parseInt(args[1]));
        NodeConfig.getInstance().setUsername(args[2]);
        Connector connector = UDPConnector.getInstance();

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

        while (Files.getInstance().getLocalFileList().size() < 4) {
            Files.getInstance().insert(getRandomElement(initialFileList));
        }

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
            Controller.register(args[0], Integer.parseInt(args[1]), args[2]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            Scanner scanner = new Scanner(System.in);
            if ("exit".equals(scanner.nextLine())) {
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
        }
    }

    //simple function to echo data to terminal
    public static void echo(String msg) {
        System.out.println(msg);
    }

    public static String getRandomElement(List<String> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }
}
