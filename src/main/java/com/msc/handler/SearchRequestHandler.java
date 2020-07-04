package com.msc.handler;

import com.msc.model.Files;

import java.util.List;

/**
 * This class is used to handle search request messages.
 */
public class SearchRequestHandler implements IncomingMsgHandler {

    @Override
    public void handle(String message) {

        List<String> localFileList = Files.getInstance().getLocalFileList();


    }
}
