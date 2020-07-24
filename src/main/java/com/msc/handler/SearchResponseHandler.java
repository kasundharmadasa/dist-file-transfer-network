package com.msc.handler;

import com.msc.Controller;
import com.msc.config.NodeConfig;
import com.msc.model.LocalIndex;
import com.msc.model.LocalIndexTable;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class is used to handle search response messages.
 */
public class SearchResponseHandler implements IncomingMsgHandler {

    @Override
    public void handle(String message, String sourceIp, Integer sourcePort) {
        StringTokenizer stringTokenizer = new StringTokenizer(message, " ");
        int length = Integer.parseInt(stringTokenizer.nextToken());
        String command = stringTokenizer.nextToken();

        String fileCount = stringTokenizer.nextToken();

        String ip = stringTokenizer.nextToken();
        String port = stringTokenizer.nextToken();
        String hops = stringTokenizer.nextToken();

        List<String> fileList = new ArrayList<>();
        String[] files = message.split("\"");

        if (Integer.parseInt(fileCount) > 0) {

            for (int i = 1; i < files.length; i++) {
                if (StringUtils.isNotBlank(files[i].trim())) {
                    fileList.add(files[i].trim());
                }
            }

            List<List<String>> fileListWithSeparatedFileNames = new ArrayList<>();
            for (String fileName : fileList) {
                List<String> fileNamesSeperatedList = Arrays.asList(fileName.split(" "));
                fileListWithSeparatedFileNames.add(fileNamesSeperatedList);
            }

            if (NodeConfig.getInstance().isSearchCacheEnabled()) {
                boolean foundEntry = false;
                for (LocalIndex localIndex : LocalIndexTable.getInstance().getLocalIndexList()) {
                    if (ip.equals(localIndex.getIp()) && Integer.parseInt(port) == localIndex.getTcpPort()) {

                        // Cache search result to the local index table.
                        localIndex.getFiles().addAll(fileListWithSeparatedFileNames);
                        foundEntry = true;
                        break;
                    }
                }

                if (!foundEntry) {
                    LocalIndexTable.getInstance().insert(new LocalIndex(ip, sourcePort, Integer.parseInt(port),
                            fileListWithSeparatedFileNames, Integer.parseInt(hops)));
                }
            }

            for (String fileName : fileList) {
                System.out.println("found file " + fileName + " in the node " + ip + ":" + port + " with " + hops +
                        " hops");
            }

            if (!fileList.isEmpty()) {

                System.out.println("Sending file download request for " + fileList.get(0));
                Controller.download(fileList.get(0), ip, port);
            }

        } else {
            System.out.println("file not found");
        }
    }
}
