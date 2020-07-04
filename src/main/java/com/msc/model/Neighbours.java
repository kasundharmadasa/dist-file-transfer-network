package com.msc.model;

import java.util.ArrayList;
import java.util.List;

public class Neighbours {

    private static List<Node> neighbourList;
    private static volatile Neighbours neighbours;

    static {
        neighbourList = new ArrayList<>();
    }

    private Neighbours() {
    }

    public static Neighbours getInstance() {
        if (neighbours == null) {
            synchronized (Neighbours.class) {
                if (neighbours == null) {
                    neighbours = new Neighbours();
                }
            }
        }
        return neighbours;
    }

    public void insert(Node node) {
        System.out.println("Node " + node.getNodeIp() + ":" + node.getPort());
        neighbourList.add(node);

    }

    public List<Node> getPeerNodeList() {
        return neighbourList;
    }

    public boolean remove(Node node) {
        return neighbourList.remove(node);
    }

    public void search() {

    }

}
