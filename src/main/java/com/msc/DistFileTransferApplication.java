package com.msc;

import com.msc.config.FileStorageProperties;
import com.msc.config.NodeConfig;
import com.msc.connector.Connector;
import com.msc.connector.UDPConnector;
import com.msc.model.CommonConstants;
import com.msc.model.LocalIndex;
import com.msc.model.LocalIndexTable;
import com.msc.model.LocalStorage;
import com.msc.model.Neighbours;
import com.msc.model.Node;
import com.msc.model.SearchRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Future;

@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
public class DistFileTransferApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistFileTransferApplication.class, args);

        // Add the node configurations

        List<String> argsList = Arrays.asList(args);
        parseCommandLineArgs(argsList);

        Connector connector = UDPConnector.getInstance();

        // Select files to be hosted by the server.
        List<List<String>> selectedFiles = selectFilesForNode();

        System.out.println("Listing on port " + NodeConfig.getInstance().getUdpPort() + " ....");

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

        // Send heartbeat
        new Thread(() -> {
            while (true) {
                sendHearbeat();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        ).start();


        // Register in the Bootstrap Server
        BSRegister();


        while (true) {

            // Listen for console commands
            listenForCommands();
        }
    }

    /**
     * Initiate the search request
     *
     * @param searchQuery
     */
    private static void initiateSearch(String searchQuery) {

        SearchRequest searchRequest = new SearchRequest(NodeConfig.getInstance().getIp(),
                NodeConfig.getInstance().getUdpPort(),
                NodeConfig.getInstance().getIp(),
                NodeConfig.getInstance().getUdpPort(), searchQuery, 1);

        try {
            Controller.search(searchRequest);
        } catch (IOException e) {
            e.printStackTrace();
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
        while (selectedFileIndexes.size() < CommonConstants.FILES_PER_NODE) {
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

            LocalStorage.getInstance().getLocalFileList().add(selectedFile);
        }

        LocalIndexTable.getInstance().insert(new LocalIndex(NodeConfig.getInstance().getIp(),
                NodeConfig.getInstance().getUdpPort(), NodeConfig.getInstance().getTcpPort(), selectedFiles, 0));

        return selectedFiles;
    }

    private static List<String> selectSearchQueries() {

        List<String> queriesList = Arrays.asList("Twilight",
                "Jack",
                "American Idol",
                "Happy Feet",
                "Twilight saga",
                "Happy Feet",
                "Happy Feet",
                "Feet",
                "Happy Feet",
                "Twilight",
                "Windows",
                "Happy Feet",
                "Mission Impossible",
                "Twilight",
                "Windows 8",
                "The",
                "Happy",
                "Windows 8",
                "Happy Feet",
                "Super Mario",
                "Jack and Jill",
                "Happy Feet",
                "Impossible",
                "Happy Feet",
                "Turn Up The Music",
                "Adventures of Tintin",
                "Twilight saga",
                "Happy Feet",
                "Super Mario",
                "American Pickers",
                "Microsoft Office 2010",
                "Twilight",
                "Modern Family",
                "Jack and Jill",
                "Jill",
                "Glee",
                "The Vampire Diarie",
                "King Arthur",
                "Jack and Jill",
                "King Arthur",
                "Windows XP",
                "Harry Potter",
                "Feet",
                "Kung Fu Panda",
                "Lady Gaga",
                "Gaga",
                "Happy Feet",
                "Twilight",
                "Hacking",
                "King");

        List<Integer> selectedQueryIndexes = new ArrayList<>();
        while (selectedQueryIndexes.size() < CommonConstants.SEARCH_QUERIES_PER_ODE) {
            Integer randIndex = new Random().nextInt(queriesList.size());
            if (!selectedQueryIndexes.contains(randIndex)) {
                selectedQueryIndexes.add(randIndex);
            }
        }

        List<String> selectedQueries = new ArrayList<>();
        for (Integer selectedFileIndex : selectedQueryIndexes) {
            System.out.println("Selected Query: " + queriesList.get(selectedFileIndex));
            selectedQueries.add(queriesList.get(selectedFileIndex));
        }

        return selectedQueries;
    }

    private static void sendHearbeat() {
        List<LocalIndex> localIndices = LocalIndexTable.getInstance().getLocalIndexList();
        List<Integer> indexesTobeRemoved = new ArrayList<>();
        for (int index = 0; index < localIndices.size(); index++) {

            LocalIndex localIndex = localIndices.get(index);
            if (!(localIndex.getIp() + ":" + localIndex.getTcpPort()).equals(NodeConfig.getInstance().getIp() +
                    ":" + NodeConfig.getInstance().getTcpPort())) {
                if (Controller.ping(localIndex.getIp(), localIndex.getTcpPort()) != 200) {
                    indexesTobeRemoved.add(index);
                } else {
                    System.out.println("ping successful for " + localIndex.getIp() + ":" + localIndex.getPort());
                }
            }
        }

        for (int index : indexesTobeRemoved) {
            LocalIndexTable.getInstance().remove(index);
        }
    }

    private static void parseCommandLineArgs(List<String> args) {
        for (String arg : args) {
            if (arg.contains("ip")) {
                NodeConfig.getInstance().setIp(arg.substring(arg.lastIndexOf("=") + 1));
            } else if (arg.contains("udp.port")) {
                NodeConfig.getInstance().setUdpPort(Integer.parseInt(arg.substring(arg
                        .lastIndexOf("=") + 1)));
            } else if (arg.contains("server.port")) {
                NodeConfig.getInstance().setTcpPort(Integer.parseInt(arg.substring(arg
                        .lastIndexOf("=") + 1)));
            } else if (arg.contains("username")) {
                NodeConfig.getInstance().setUsername(arg.substring(arg.lastIndexOf("=") + 1));
            } else if (arg.contains("bs.ip")) {
                NodeConfig.getInstance().setBootstrapServerIp(arg.substring(arg.lastIndexOf("=") + 1));
            } else if (arg.contains("bs.port")) {
                NodeConfig.getInstance().setBootstrapServerPort(Integer.parseInt(arg.substring(arg
                        .lastIndexOf("=") + 1)));
            } else if (arg.contains("cache.enabled")) {
                NodeConfig.getInstance().setSearchCacheEnabled(Boolean.parseBoolean(arg.substring(arg
                        .lastIndexOf("=") + 1)));
            }
        }
    }

    private static void BSRegister() {
        try {
            Controller.register(NodeConfig.getInstance().getIp(), NodeConfig.getInstance().getUdpPort(),
                    NodeConfig.getInstance().getUsername());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void listenForCommands() {
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

        // search from command
        if (command.startsWith("search")) {
            String searchString = command.substring(command.indexOf(" ") + 1);
            initiateSearch(searchString);
        }

        // initiate random search
        if ("rand search".equals(command)) {
            try {
                List<String> selectedQueries = selectSearchQueries();
                for (String searchQuery : selectedQueries) {
                    System.out.println("Initiating search for " + searchQuery);
                    initiateSearch(searchQuery);
                    Thread.sleep(10000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // initiate random search
        if (command.startsWith("download")) {
            try {
                List<String> selectedQueries = selectSearchQueries();
                for (String searchQuery : selectedQueries) {
                    System.out.println("Initiating search for " + searchQuery);
                    initiateSearch(searchQuery);
                    Thread.sleep(10000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //simple function to echo data to terminal
    public static void echo(String msg) {
        System.out.println(msg);
    }
}
