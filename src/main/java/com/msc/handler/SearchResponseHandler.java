package com.msc.handler;

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
                for (LocalIndex localIndex : LocalIndexTable.getInstance().getLocalIndexList()) {
                    if (ip.equals(localIndex.getIp()) && Integer.parseInt(port) == localIndex.getPort()) {

                        // Cache search result to the local index table.
                        localIndex.getFiles().addAll(fileListWithSeparatedFileNames);
                        break;
                    }
                }
            }

            for (String fileName : fileList) {
                System.out.println("found file " + fileName + " in the node " + ip + ":" + port + " with " + hops +
                        " hops");
            }
        } else {
            System.out.println("file not found");
        }
    }
}
