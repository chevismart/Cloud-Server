package com.gamecenter.utils;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.gamecenter.model.DeviceInfo;
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

    public static boolean removeSession(IoSession ioSession) {
        Map<String, DeviceInfo> deviceInfoMap = Initialization.getInstance().getClientMap();
        for (Map.Entry<String, DeviceInfo> deviceInfo : deviceInfoMap.entrySet()) {
            if (deviceInfo.getValue().getSession().equals(ioSession)) {
                deviceInfoMap.remove(deviceInfo);
                return true;
            }
        }
        return false;
    }

    public static String createSessionKey(byte[] centerId, byte[] macAdd) {
        return ByteArrayUtil.toHexString(centerId).toLowerCase() + ByteArrayUtil.toHexString(macAdd).toLowerCase();
    }

    public static String getMacFromSessionKey(String sessionKey, String key) {
        return sessionKey.toLowerCase().replace(key.toLowerCase(), StringUtils.EMPTY);
    }

    public static DeviceInfo getDeviceInfoByIoSession(IoSession ioSession) {
        for (DeviceInfo deviceInfo : Initialization.getInstance().getClientMap().values()) {
            if (deviceInfo.getSession().equals(ioSession)) return deviceInfo;
        }
        return null;
    }

    public static Map<String, DeviceInfo> getDeviceInfoByCenterId(byte[] centerId) {
        return getSessionByFinder(centerIdFinder, centerId);
    }

    public static Map<String, DeviceInfo> getDeviceInfoByCenterId(String centerId) {
        return getDeviceInfoByFinder(centerIdFinder, centerId);
    }

    public static Map<String, DeviceInfo> getDeviceInfoByMacAddress(byte[] mac) {
        return getSessionByFinder(macFinder, mac);
    }

    public static Map<String, DeviceInfo> getDeviceInfoByMacAddress(String mac) {
        return getDeviceInfoByFinder(macFinder, mac);
    }

    protected static Map<String, DeviceInfo> getSessionByFinder(SessionFinder sessionFinder, byte[] key) {
        return getDeviceInfoByFinder(sessionFinder, ByteArrayUtil.toHexString(key));
    }

    protected static Map<String, DeviceInfo> getDeviceInfoByFinder(SessionFinder sessionFinder, String key) {
        HashMap<String, DeviceInfo> sessionHashMap = Initialization.getInstance().getClientMap();
        HashMap<String, DeviceInfo> resultMap = new HashMap<String, DeviceInfo>();
        for (String sessionId : sessionHashMap.keySet()) {
            if (sessionFinder.find(sessionId, key)) {
                resultMap.put(sessionId, sessionHashMap.get(sessionId));
            }
        }
        return resultMap;
    }


    private static interface SessionFinder {
        boolean find(String session, String key);
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
}
