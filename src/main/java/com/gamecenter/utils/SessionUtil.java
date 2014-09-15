package com.gamecenter.utils;

import ch.qos.logback.core.encoder.ByteArrayUtil;

/**
 * Created by Chevis on 14-9-10.
 */
public class SessionUtil {

    public static String createSessionKey(byte[] centerId, byte[] macAdd) {
        return ByteArrayUtil.toHexString(centerId) + ByteArrayUtil.toHexString(macAdd);
    }
}
