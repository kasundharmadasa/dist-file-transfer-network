package com.msc.handler;

/**
 * This interface will be used to handler all the incoming messages.
 */
public interface IncomingMsgHandler {

    public void handle(String message, String sourceIp, Integer sourcePort);

}
