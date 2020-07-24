package com.msc;

import com.msc.config.NodeConfig;
import com.msc.connector.Connector;
import com.msc.connector.UDPConnector;
import com.msc.model.CommonConstants;
import com.msc.model.LocalIndex;
import com.msc.model.LocalIndexTable;
import com.msc.model.MessageConstants;
import com.msc.model.Neighbours;
import com.msc.model.Node;
import com.msc.model.SearchRequest;
import com.msc.model.SearchRequests;
import com.msc.util.CommonUtil;
import com.msc.util.MessageUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Controller {

    private static Connector connector = UDPConnector.getInstance();

    public static void register(String nodeIP, Integer nodePort, String nodeUsername) throws IOException {

        String message = MessageUtil.generateInitMessage(MessageConstants.REG_MESSAGE, nodeIP, nodePort, nodeUsername);
        connector.send(message, InetAddress.getByName(NodeConfig.getInstance().getBootstrapServerIp()),
                NodeConfig.getInstance().getBootstrapServerPort());
    }

    public static void unregister() throws IOException {

        String message = MessageUtil.generateInitMessage(MessageConstants.UNREG_MESSAGE, NodeConfig.getInstance().getIp(),
                NodeConfig.getInstance().getUdpPort(), NodeConfig.getInstance().getUsername());
        connector.send(message, InetAddress.getByName(NodeConfig.getInstance().getBootstrapServerIp()),
                NodeConfig.getInstance().getBootstrapServerPort());
    }

    public static void join(String ip, int port) throws IOException {

        String message = MessageUtil.generateInitMessage(MessageConstants.JOIN_MESSAGE, NodeConfig.getInstance().getIp(),
                NodeConfig.getInstance().getUdpPort());
        connector.send(message, InetAddress.getByName(ip), port);

    }

    public static void leave(String ip, int port) throws IOException {

        String message = MessageUtil.generateInitMessage(MessageConstants.LEAVE_MESSAGE, NodeConfig.getInstance().getIp(),
                NodeConfig.getInstance().getUdpPort());
        connector.send(message, InetAddress.getByName(ip), port);

    }

    public static void search(SearchRequest searchRequest) throws IOException {

        String searchRequestKey = CommonUtil.generateSearchRequestKey(searchRequest);

        if (!SearchRequests.getInstance().getSearchRequestMap().containsKey(searchRequestKey)) {

            // add to the local search request table
            SearchRequests.getInstance().insert(searchRequestKey, searchRequest);

            String message = MessageUtil.generateSearchMessage(searchRequest.getInitiatedIp(),
                    searchRequest.getInitiatedPort(), searchRequest.getSearchString(), searchRequest.getHops() + 1);

            // search the local index table for matching file entries.
            for (LocalIndex localIndex : LocalIndexTable.getInstance().getLocalIndexList()) {

                List<String> searchWords = Arrays.asList(searchRequest.getSearchString().split(" "));

                List<String> matchedFiles = new ArrayList<>();
                for (String word : searchWords) {
                    for (List<String> fileNameList : localIndex.getFiles()) {
                        if (fileNameList.stream().anyMatch(word::equalsIgnoreCase)) {
                            matchedFiles.add(String.join(" ", fileNameList).trim());
                        }
                    }
                }

                Set<String> set = new HashSet<>(matchedFiles);
                matchedFiles.clear();
                matchedFiles.addAll(set);

                if (!matchedFiles.isEmpty()) {
                    if (localIndex.getIp().equals(NodeConfig.getInstance().getIp()) &&
                            localIndex.getPort().equals(NodeConfig.getInstance().getUdpPort())) {

                        // if the matched entry is from the this node, send the search response.
                        Controller.searchResponse(searchRequest.getInitiatedIp(), searchRequest.getInitiatedPort(),
                                matchedFiles, searchRequest.getHops());
                    } else {
                        // if the matched entry is not from this node, send the search message directly to the
                        // relevant node.
                        System.out.println("Sending search message " + message + " to "
                                + localIndex.getIp() + ":" + localIndex.getPort());
                        connector.send(message, InetAddress.getByName(localIndex.getIp()), localIndex.getPort());
                    }
                    return;
                }
            }

            // if the files cannot be found from the local index table, send the search request to neighbours
            sendSearchReqToNeighbours(searchRequest, message);

        }

    }

    public static void download(String filename, String ip, String port) {

        HttpGet request = null;
        try {
            String url = "http://" + ip + ":" + port +  "/download/" + URLEncoder.encode(filename, "UTF-8");
            request = new HttpGet(url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // return it as a String
                int result = response.getStatusLine().getStatusCode();
                System.out.println("Received response with a status code " + result);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int ping(String ip, Integer port) {
        String url = "http://" + ip + ":" + port + "/ping";
        HttpGet request = new HttpGet(url);

        System.out.println("Sending ping message to " + url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // return it as a String
                int result = response.getStatusLine().getStatusCode();
                return result;
            }

        } catch (IOException e) {
            // ignored
        }
        return -1;
    }

    private static void sendSearchReqToNeighbours(SearchRequest searchRequest, String message) throws IOException {

        List<Node> neighbours = new ArrayList<>();
        neighbours.addAll(Neighbours.getInstance().getPeerNodeList());

        for (Node neighbour : neighbours) {
            if (searchRequest.getReceivedIp().equals(neighbour.getNodeIp()) &&
                    searchRequest.getReceivedPort() == neighbour.getPort()) {
                neighbours.remove(neighbour);
                break;
            }
        }

        if (neighbours.size() <= CommonConstants.SEARCH_MSG_FORWARDING_LIMIT) {
            for (Node neighbour : neighbours) {
                System.out.println("Sending search message " + message + " to "
                        + neighbour.getNodeIp() + ":" + neighbour.getPort());
                connector.send(message, InetAddress.getByName(neighbour.getNodeIp()), neighbour.getPort());
            }
        } else {
            List<Integer> randNodeIndexes = new ArrayList<>();

            while (randNodeIndexes.size() < CommonConstants.SEARCH_MSG_FORWARDING_LIMIT) {
                Integer randIndex = new Random().nextInt(neighbours.size());
                if (!randNodeIndexes.contains(randIndex)) {
                    randNodeIndexes.add(randIndex);
                }
            }

            for (Integer randIndex : randNodeIndexes) {
                Node randomNeighbour = neighbours.get(randIndex);

                System.out.println("Sending search message " + message + " to " +
                        randomNeighbour.getNodeIp() + ":" + randomNeighbour.getPort());
                connector.send(message, InetAddress.getByName(randomNeighbour.getNodeIp()),
                        randomNeighbour.getPort());
            }
        }
    }

    public static void searchResponse(String ip, int port, List<String> matchedFiles, Integer hops) throws IOException {

        String message = MessageUtil.generateSearchResponseMessage(NodeConfig.getInstance().getIp(),
                NodeConfig.getInstance().getTcpPort(), matchedFiles, hops);

        System.out.println("Sending search response message " + message + " to " + ip + ":" + port);
        connector.send(message, InetAddress.getByName(ip), port);

    }

}
