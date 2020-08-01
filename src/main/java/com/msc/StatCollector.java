package com.msc;

import com.msc.config.NodeConfig;
import com.msc.model.Neighbours;
import com.msc.model.Node;
import com.msc.model.Search;
import com.msc.model.SearchRequest;
import com.msc.model.SearchResponse;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StatCollector {

    private static List<SearchRequest> nodeMessages;
    private static volatile StatCollector statCollector;
    private static List<Search> initiatedSearches;
    private static List<SearchResponse> searchResponses;

    static {
        nodeMessages = new ArrayList<>();
        initiatedSearches = new ArrayList<>();
        searchResponses = new ArrayList<>();
    }

    private StatCollector() {

    }

    public static StatCollector getInstance() {
        if (statCollector == null) {
            synchronized (StatCollector.class) {
                if (statCollector == null) {
                    statCollector = new StatCollector();
                }
            }
        }
        return statCollector;
    }

    public void publish() {

        publishNodeMessages();

        publishInitiatedSearches();

        publishSearchResponses();

        publishNeighbours();

    }

    private void publishNeighbours() {

        File file = new File(System.getProperty("user.dir") + File.separator + NodeConfig.getInstance().getUdpPort() +
                "-neighbours.csv");
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

            String[] header = { "Neighbour Count"};
            writer.writeNext(header);

                String[] data = {((Integer) Neighbours.getInstance().getPeerNodeList().size()).toString()};

                writer.writeNext(data);


            // closing writer connection
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void publishNodeMessages() {

        File file = new File(System.getProperty("user.dir") + File.separator + NodeConfig.getInstance().getUdpPort() +
                "-messages.csv");
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

            String[] header = { "Initiated IP", "Initiated Port", "Received IP", "Received Port", "Search String",
            "Hops"};
            writer.writeNext(header);

            for (SearchRequest searchRequest : nodeMessages) {
                String[] data = {searchRequest.getInitiatedIp(), searchRequest.getInitiatedPort().toString(),
                        searchRequest.getReceivedIp(), searchRequest.getReceivedPort().toString(),
                        searchRequest.getSearchString(), searchRequest.getHops().toString()};

                writer.writeNext(data);
            }

            // closing writer connection
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void publishInitiatedSearches() {

        File file =
                new File(System.getProperty("user.dir") + File.separator + NodeConfig.getInstance().getUdpPort()
                        + "-initiatedSearches.csv");
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

            String[] header = { "Initiated Timestamp", "Search String"};
            writer.writeNext(header);
            for (Search search : initiatedSearches) {
                String[] data = {search.initiatedTimestamp.toString(), search.searchString};

                writer.writeNext(data);
            }

            // closing writer connection
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void publishSearchResponses() {

        File file =
                new File(System.getProperty("user.dir") + File.separator + NodeConfig.getInstance().getUdpPort() +
                        "-searchResponses.csv");
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

            String[] header = { "Received Timestamp", "Received File names", "Hops"};
            writer.writeNext(header);

            for (SearchResponse searchResponse : searchResponses) {

                String[] data = {searchResponse.receivedTimestamp.toString(),
                        String.join(" ", searchResponse.receivedFileNames),
                        searchResponse.getHops().toString()};

                writer.writeNext(data);
            }

            // closing writer connection
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<SearchRequest> getNodeMessages() {
        return nodeMessages;
    }

    public void setNodeMessages(List<SearchRequest> nodeMessages) {
        StatCollector.nodeMessages = nodeMessages;
    }

    public List<Search> getInitiatedSearches() {
        return initiatedSearches;
    }

    public void setInitiatedSearches(List<Search> initiatedSearches) {
        StatCollector.initiatedSearches = initiatedSearches;
    }

    public List<SearchResponse> getSearchResponses() {
        return searchResponses;
    }

    public void setSearchResponses(List<SearchResponse> searchResponses) {
        StatCollector.searchResponses = searchResponses;
    }

}
