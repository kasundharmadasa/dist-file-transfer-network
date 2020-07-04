package com.msc.model;

public class CommonConstants {

    public static final int MSG_LENGTH_FIELD_SIZE = 4;
    public static final int PORT = randomWithRange(45000, 55000);
    public static final String USER_NAME = "vin";
    public static final int HOPS = 10;
    public static final int BS_SERVER_PORT = 55555;
    public static final int HEART_BEAT_SEND_THRESHOLD = 5; //in seconds
    public static final int HEART_BEAT_RECEIVE_THRESHOLD = 30; //in seconds
    public static final int HEART_BEAT_CLEAR_THRESHOLD = 100; //in seconds
    public static final String IP_ADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    public static String IP = "127.0.0.1";
    public static String BootstrapServerIp = "";

    public static int randomWithRange(int min, int max) {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }
    public static volatile Boolean isRegisterd = false;
}
