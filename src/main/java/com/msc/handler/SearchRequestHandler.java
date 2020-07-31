package com.msc.handler;

import com.msc.Controller;
import com.msc.StatCollector;
import com.msc.model.CommonConstants;
import com.msc.model.SearchRequest;

import java.io.IOException;
import java.util.Collections;
import java.util.StringTokenizer;

/**
 * This class is used to handle search request messages.
 */
public class SearchRequestHandler implements IncomingMsgHandler {

    @Override
    public void handle(String message, String sourceIp, Integer sourcePort) {

        StringTokenizer stringTokenizer = new StringTokenizer(message, " ");
        int length = Integer.parseInt(stringTokenizer.nextToken());
        String command = stringTokenizer.nextToken();

        String ip = stringTokenizer.nextToken();
        String port = stringTokenizer.nextToken();

        String searchString = stringTokenizer.nextToken();
        StringBuilder stringBuilder = new StringBuilder(searchString);

        while (!searchString.endsWith("\"")) {
            searchString = stringTokenizer.nextToken();
            stringBuilder.append(" ").append(searchString);
        }

        String hops = stringTokenizer.nextToken();
        hops = hops.replaceAll("[^\\d.]", "");

        SearchRequest searchRequest = new SearchRequest(ip, Integer.parseInt(port),
                sourceIp, sourcePort, stringBuilder.toString()
                .replace("\"", ""), Integer.parseInt(hops));

        // Add request to stat collector
        StatCollector.getInstance().getNodeMessages().add(searchRequest);
        try {
            if (Integer.parseInt(hops) > CommonConstants.HOPS) {
                Controller.searchResponse(ip, Integer.parseInt(port), Collections.emptyList(), Integer.parseInt(hops));
            } else {
                Controller.search(searchRequest);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
