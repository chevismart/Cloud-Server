package com.gamecenter.handler.tcp;

import com.gamecenter.model.DeviceInfo;
import com.gamecenter.utils.SessionUtil;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chevis on 14-9-19.
 */
public class DeviceListProxy extends DeviceProxy {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<String, List<IoSession>> onlineDevicesMap;

    public Map<String, List<IoSession>> getOnlineDevicesMap() {
        return onlineDevicesMap;
    }

    public void setOnlineDevicesMap(Map<String, List<IoSession>> onlineDevicesMap) {
        this.onlineDevicesMap = onlineDevicesMap;
    }

    public void putOrNewOnlineDeviceMap(String centreId, IoSession ioSession) {
        if (null == onlineDevicesMap) {
            onlineDevicesMap = new HashMap<String, List<IoSession>>();
        }
        if (null == onlineDevicesMap.get(centreId)) {
            onlineDevicesMap.put(centreId, new ArrayList<IoSession>());
        }
        List<IoSession> deviceList = onlineDevicesMap.get(centreId);
        deviceList.add(ioSession);
    }

    public boolean removeDeviceOnlineDeviceMap(String centerId, IoSession ioSession) {
        if (null == onlineDevicesMap || null == onlineDevicesMap.get(centerId)) {
            logger.warn("There is no online device for centerId {}!", centerId);
            return true;
        } else {
            List<IoSession> deviceList = onlineDevicesMap.get(centerId);
            return deviceList.contains(ioSession) ? deviceList.remove(ioSession) : true;
        }
    }

    public List<String> getOninceDevicesByCenterId(String centerId) {
        Map<String, DeviceInfo> deviceSessions = SessionUtil.getDeviceInfoByCenterId(centerId);

        List<String> macList = new ArrayList<String>();

        for (String sessionKey : deviceSessions.keySet()) {
            macList.add(SessionUtil.getMacFromSessionKey(sessionKey, centerId));
        }

        return macList;
    }

    @Override
    IoFuture execute(byte[] message, IoSession ioSession) {
        return null;
    }
}
