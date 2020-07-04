package com.msc.handler;

import com.msc.Controller;
import com.msc.model.Neighbours;
import com.msc.model.Node;

import java.io.IOException;
import java.util.StringTokenizer;

public class RegResponseHandler implements MsgResponseHandler {

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
                    neighbourTable.insert(new Node(ip, Integer.parseInt(port)));

                    try {
                        System.out.println("Send join message: " + ip + ":" + port);
                        Controller.join(ip, Integer.parseInt(port));
                        System.out.println("Joined: " + ip + ":" + port);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }



  /*  @Override
    public void sendHeartBeatSignal() {
        Set<Node> neighbourNodes = NeighbourTableImpl.getInstance().getNeighbourNodeList();
        if (!neighbourNodes.isEmpty()) {
            HeartBeatSignalModel heartBeatSignalModel = new HeartBeatSignalModel(ApplicationConstants.IP, ApplicationConstants.PORT, ApplicationConstants.USER_NAME);
            for (Node neighbour : neighbourNodes) {
                String heartBeatMessage = messageBuilder.buildHeartBeatSignalMessage(heartBeatSignalModel);
                try {
                    System.out.println("Sending HBEAT to "+neighbour.getNodeIp()+" "+ neighbour.getPort() +"by" + ApplicationConstants.IP + " " + String.valueOf(ApplicationConstants.PORT) + " " + ApplicationConstants.USER_NAME);
                    udpConnector.send(heartBeatMessage, InetAddress.getByName(neighbour.getNodeIp()), neighbour.getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void gracefulLeaveRequest() {
        NeighbourTableImpl neighbourTable = NeighbourTableImpl.getInstance();

        //First Unreg from the Bootstrap server
        GracefulLeaveBootstrapServerRequestModel gracefulLeaveBootstrapServerRequestModel = new GracefulLeaveBootstrapServerRequestModel(ApplicationConstants.IP, ApplicationConstants.PORT, ApplicationConstants.USER_NAME);
        String message = messageBuilder.buildUnregisterRequestMessage(gracefulLeaveBootstrapServerRequestModel);
        try {
            udpConnector.send(message, InetAddress.getByName(ApplicationConstants.BootstrapServerIp), ApplicationConstants.BS_SERVER_PORT);
//            GUIController.getInstance().displayMessage("Successfully Un-Registered from the Bootstrap Server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Next, Notify Neighbours of our departure

    }

    @Override
    public void sendLeaveOkToSource(GracefulLeaveResponseModel gracefulLeaveResponseModel) throws IOException {
        String buildLeaveOkToSourceMessage = messageBuilder.buildLeaveOkToSourceMessage(gracefulLeaveResponseModel);
        udpConnector.send(buildLeaveOkToSourceMessage, InetAddress.getByName(gracefulLeaveResponseModel.getIp()),gracefulLeaveResponseModel.getPort());
    }

    @Override
    public void searchFile(String file) {
        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(new Node(ApplicationConstants.IP, ApplicationConstants.PORT));
        SearchRequestModel searchRequestModel = new SearchRequestModel(ApplicationConstants.IP, ApplicationConstants.PORT, file, ApplicationConstants.HOPS, nodes);
        searchRequestModel.handle();
        //forward to two picked nodes
    }

    @Override
    public void sendSearchRequest(SearchRequestModel model, ConcurrentLinkedQueue<Node> statTablePeers) throws IOException {
        String searchRequestMessage = messageBuilder.buildSearchRequestMessage(model);

        System.out.println("Found stat table entries");
        System.out.println(statTablePeers);

        GUIController guiController = GUIController.getInstance();

        Iterator<Node> nodeIterator = statTablePeers.iterator();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            if (!model.getLastHops().contains(node)) {
                udpConnector.send(searchRequestMessage, InetAddress.getByName(node.getNodeIp()), node.getPort());
                guiController.updateQueryMessageForwarded();
            }
            System.out.println("send to stat table entries " + node.getPort());
        }

        final Set<Node> peerNodeList = PeerTableImpl.getInstance().getPeerNodeList();

        final ArrayList<Node> peerNodeListToSend = new ArrayList<>();

        peerNodeList.forEach((node) -> {
            if (!model.getLastHops().contains(node) && !statTablePeers.contains(node)) {
                peerNodeListToSend.add(node);
            }
        });

        System.out.println("peer nodes to send list " + peerNodeListToSend);
        Random random = new Random();
        int size = peerNodeListToSend.size();
        if (size > 0) {
            final int item1 = random.nextInt(size);
            Node node = peerNodeListToSend.get(item1);
            udpConnector.send(searchRequestMessage, InetAddress.getByName(node.getNodeIp()), node.getPort());
//            System.out.println("Sending to peer node " + peerNodeListToSend.get(item1).getPort());
            peerNodeListToSend.remove(item1);
            guiController.updateQueryMessageForwarded();
        }
        size = peerNodeListToSend.size();
        if (size > 0) {
            final int item2 = random.nextInt(size);
            Node node = peerNodeListToSend.get(item2);
            udpConnector.send(searchRequestMessage, InetAddress.getByName(node.getNodeIp()), node.getPort());
//            System.out.println("Sending to peer node " + peerNodeListToSend.get(item2).getPort());
            guiController.updateQueryMessageForwarded();
        }
    }

    @Override
    public void sendLocalSearchToSource(SearchResponseModel searchResponseModel, List<String> list) throws IOException {
        String searchResponseToSourceMessage = messageBuilder.buildSearchResponseToSourceMessage(searchResponseModel);
        udpConnector.send(searchResponseToSourceMessage, InetAddress.getByName(searchResponseModel.getIp()), searchResponseModel.getPort());
    }

    //check whether the stat table entry equals to the node which request the file
    private boolean isRequestingNode(SearchRequestModel searchRequestModel, Node node) {
        return searchRequestModel.getFileName().equals(node.getNodeIp()) && searchRequestModel.getPort() == node.getPort();
    }



    public void notifyNeighbourLeave(Set<Node> nodes) throws IOException {
        nodes.forEach(node -> {
            GracefulLeaveRequestModel gracefulLeaveRequestModel = new GracefulLeaveRequestModel(ApplicationConstants.IP, ApplicationConstants.PORT);
            String neighbourLeaveMessage = new MessageBuilderImpl().buildLeaveMessage(gracefulLeaveRequestModel);
            try {
                udpConnector.send(neighbourLeaveMessage, InetAddress.getByName(node.getNodeIp()), node.getPort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
*/

    }
