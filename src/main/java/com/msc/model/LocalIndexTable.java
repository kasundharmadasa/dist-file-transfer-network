package com.msc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is used to store the local search indexes known by the node.
 */
public class LocalIndexTable {

    private static List<LocalIndex> localIndexList;
    private static volatile LocalIndexTable localIndexTable;

    static {
        localIndexList = new ArrayList<>();
    } 

    private LocalIndexTable() {
    }

    public static LocalIndexTable getInstance() {
        if (localIndexTable == null) {
            synchronized (LocalIndexTable.class) {
                if (localIndexTable == null) {
                    localIndexTable = new LocalIndexTable();
                }
            }
        }
        return localIndexTable;
    }

    public void insert(LocalIndex localIndex) {
        localIndexList.add(localIndex);
        Collections.sort(localIndexList);
    }

    public void remove(int index) {
        this.localIndexList.remove(index);
        Collections.sort(localIndexList);
    }

    public List<LocalIndex> getLocalIndexList() {
        return localIndexList;
    }

}
