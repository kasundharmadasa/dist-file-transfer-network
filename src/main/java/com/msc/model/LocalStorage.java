package com.msc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is used to store the files known by the node.
 */
public class LocalStorage {

    private static List<String> localFileList;
    private static volatile LocalStorage localStorage;

    static {
        localFileList = new ArrayList<>();
    }

    private LocalStorage() {

    }

    public static LocalStorage getInstance() {
        if (localStorage == null) {
            synchronized (LocalStorage.class) {
                if (localStorage == null) {
                    localStorage = new LocalStorage();
                }
            }
        }
        return localStorage;
    }

    public void insert(String file) {
        localFileList.add(file);
        Collections.sort(localFileList);
    }

    public List<String> getLocalFileList() {
        return localFileList;
    }

}
