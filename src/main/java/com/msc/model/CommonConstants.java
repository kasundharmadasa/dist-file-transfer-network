package com.msc.model;

/**
 * This class is used to hold the common constants.
 */
public class CommonConstants {

    public static final int MSG_LENGTH_FIELD_SIZE = 4;
    public static final int HOPS = 5;
    public static final int SEARCH_MSG_FORWARDING_LIMIT = 2;
    public static final int MAX_ALLOWED_TO_JOIN = 2;
    public static final int FILES_PER_NODE = 4;
    public static final int SEARCH_QUERIES_PER_ODE = 4;

    public static final int FILE_SIZE_MAX = 10;
    public static final int FILE_SIZE_MIN = 2;

    public static final int TCP_UDP_PORT_DIFFERENCE = 20000;
    public static final int UDP_PORT_RANGE_START = 10000 + TCP_UDP_PORT_DIFFERENCE;

    public static final int SEARCH_REQUEST_CACHE_EXPIRY_MILLIS = 8000;

}
