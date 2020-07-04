package com.msc.util;

import com.msc.model.CommonConstants;
import com.msc.model.MessageConstants;

public class MessageUtil {

    public static String generateMessage(String msgType, String ip, Integer port, String... username) {

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

    public static String generateSearchMessage(String ip, Integer port, String searchString, Integer... hops) {

        String msg = new StringBuilder()
                .append(" ").append(MessageConstants.SEARCH_MESSAGE)
                .append(" ").append(ip)
                .append(" ").append(port)
                .append(" ").append(searchString).toString();

        if (hops != null) {
            msg = new StringBuilder(msg)
                    .append(" ").append(hops).toString();
        }
        String messageLength = String.format("%04d", msg.length() + CommonConstants.MSG_LENGTH_FIELD_SIZE);
        return messageLength + msg;
    }
}
