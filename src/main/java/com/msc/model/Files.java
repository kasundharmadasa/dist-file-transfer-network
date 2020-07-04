package com.msc.model;

import java.util.ArrayList;
import java.util.List;

public class Files {

    private static List<String> fileList;
    private static volatile Files files;

    static {
        fileList = new ArrayList<>();
    }

    private Files() {
    }

    public static Files getInstance() {
        if (files == null) {
            synchronized (Files.class) {
                if (files == null) {
                    files = new Files();
                }
            }
        }
        return files;
    }

    public void insert(String file) {
        System.out.println("File: " + file);
        fileList.add(file);

    }

    public List<String> getPeerNodeList() {
        return fileList;
    }

    public boolean remove(String file) {
        return fileList.remove(file);
    }

    public void search() {

    }

}
