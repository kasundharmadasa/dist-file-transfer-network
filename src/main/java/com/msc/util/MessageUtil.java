package com.msc.util;

import com.msc.model.CommonConstants;
import com.msc.model.MessageConstants;

import java.util.List;

/**
 * This is a utility class to hold the message functions.
 */
public class MessageUtil {

    /**
     * Generate reg, regok, join, joinok messages
     *
     * @param msgType
     * @param ip
     * @param port
     * @param username
     * @return
     */
    public static String generateInitMessage(String msgType, String ip, Integer port, String... username) {

        String msg = new StringBuilder()
                .append(" ").append(msgType)
                .append(" ").append(ip)
                .append(" ").append(port).toString();
        if (username != null) {
            msg = new StringBuilder(msg)
                    .append(" ").append(username).toString();
        }

        String messageLength = String.format("%04d", msg.length() + CommonConstants.MSG_LENGTH_FIELD_SIZE);
        return messageLength + msg;
    }

    /**
     * Generate search message
     *
     * @param ip
     * @param port
     * @param searchString
     * @param hops
     * @return
     */
    public static String generateSearchMessage(String ip, Integer port, String searchString, Integer... hops) {

        String msg = new StringBuilder()
                .append(" ").append(MessageConstants.SEARCH_MESSAGE)
                .append(" ").append(ip)
                .append(" ").append(port)
                .append(" \"").append(searchString)
                .append("\"").toString();

        if (hops != null) {
            msg = new StringBuilder(msg)
                    .append(" ").append(hops[0]).toString();
        }
        String messageLength = String.format("%04d", msg.length() + CommonConstants.MSG_LENGTH_FIELD_SIZE);
        return messageLength + msg;
    }

    /**
     * Generate search response message
     *
     * @param ip
     * @param port
     * @param matchedFiles
     * @return
     */
    public static String generateSearchResponseMessage(String ip, Integer port, List<String> matchedFiles,
                                                       Integer hops) {

        String msg = new StringBuilder()
                .append(" ").append(MessageConstants.SEARCHOK_MESSAGE)
                .append(" ").append(matchedFiles.size())
                .append(" ").append(ip)
                .append(" ").append(port)
                .append(" ").append(hops).toString();

        for (String matchedFile : matchedFiles) {
            msg = new StringBuilder(msg)
                    .append(" \"").append(matchedFile).append("\"").toString();
        }
        String messageLength = String.format("%04d", msg.length() + CommonConstants.MSG_LENGTH_FIELD_SIZE);
        return messageLength + msg;
    }
}
