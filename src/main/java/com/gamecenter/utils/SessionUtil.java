package com.gamecenter.utils;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.gamecenter.model.Initialization;
import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.session.IoSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chevis on 14-9-10.
 */
public class SessionUtil {

    private static MacFinder macFinder = new MacFinder();
    private static CenterIdFinder centerIdFinder = new CenterIdFinder();

    public static String createSessionKey(byte[] centerId, byte[] macAdd) {
        return ByteArrayUtil.toHexString(centerId).toLowerCase() + ByteArrayUtil.toHexString(macAdd).toLowerCase();
    }

    public static Map<String, IoSession> getSessionByCenterId(byte[] centerId) {
        return getSessionByFinder(centerIdFinder, centerId);
    }

    public static Map<String, IoSession> getSessionByCenterId(String centerId) {
        return getSessionByFinder(centerIdFinder, centerId);
    }

    public static Map<String, IoSession> getSessionByMacAddress(byte[] mac) {
        return getSessionByFinder(macFinder, mac);
    }

    public static Map<String, IoSession> getSessionByMacAddress(String mac) {
        return getSessionByFinder(macFinder, mac);
    }

    protected static Map<String, IoSession> getSessionByFinder(SessionFinder sessionFinder, byte[] key) {
        return getSessionByFinder(sessionFinder, ByteArrayUtil.toHexString(key));
    }

    protected static Map<String, IoSession> getSessionByFinder(SessionFinder sessionFinder, String key) {
        HashMap<String, IoSession> sessionHashMap = Initialization.getInstance().getClientMap();
        HashMap<String, IoSession> resultMap = new HashMap<String, IoSession>();
        for (String sessionId : sessionHashMap.keySet()) {
            if (sessionFinder.find(sessionId, key)) {
                resultMap.put(sessionId, sessionHashMap.get(sessionId));
            }
        }
        return resultMap;
    }


    private static class CenterIdFinder implements SessionFinder {
        @Override
        public boolean find(String session, String key) {
            return StringUtils.isNotEmpty(session) && StringUtils.isNotEmpty(key) ?
                    session.toLowerCase().startsWith(key.toLowerCase()) : false;
        }
    }

    private static class MacFinder implements SessionFinder {
        @Override
        public boolean find(String session, String key) {
            return StringUtils.isNotEmpty(session) && StringUtils.isNotEmpty(key) ?
                    session.toLowerCase().endsWith(key.toLowerCase()) : false;
        }
    }

    private static interface SessionFinder {
        boolean find(String session, String key);
    }
}
