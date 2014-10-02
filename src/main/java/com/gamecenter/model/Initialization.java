package com.gamecenter.model;

import java.util.HashMap;

/**
 * Created by Boss on 2014/8/6.
 */
public class Initialization {

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
