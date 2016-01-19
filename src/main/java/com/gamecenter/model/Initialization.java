package com.gamecenter.model;

import java.util.HashMap;

public class Initialization implements Model {

    private static HashMap<String, DeviceInfo> sessionHashMap;

    private static Initialization instence;

    public Initialization() {
        sessionHashMap = new HashMap<String, DeviceInfo>();
    }

    public static Initialization getInstance() {

        if (null == instence) {
            instence = new Initialization();
        }
        return instence;
    }

    public HashMap<String, DeviceInfo> getClientMap() {
        return sessionHashMap;
    }
}
