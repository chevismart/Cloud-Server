package com.gamecenter.utils;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.Initialization;
import org.apache.mina.core.session.IoSession;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SessionUtilTest {

    byte[] macInByte = new byte[]{(byte) 0xf0, (byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4, (byte) 0xff};
    String macInString = ByteArrayUtil.toHexString(macInByte);
    byte[] centerIdInByte = new byte[]{00, 01, 02, 03};
    String centerIdInString = ByteArrayUtil.toHexString(centerIdInByte);


    DeviceInfo mockDeviceInfo = mock(DeviceInfo.class);
    IoSession mockIoSession = mock(IoSession.class);
    HashMap<String, DeviceInfo> mockSessionMap = new HashMap<String, DeviceInfo>();

    @Before
    public void setUp() throws Exception {
        when(mockDeviceInfo.getSession()).thenReturn(mockIoSession);
        HashMap<String, DeviceInfo> sessionHashMap = Initialization.getInstance().getClientMap();
        sessionHashMap.put(SessionUtil.createSessionKey(centerIdInByte, macInByte), mockDeviceInfo);
        mockSessionMap.put(SessionUtil.createSessionKey(centerIdInByte, macInByte), mockDeviceInfo);
    }

    @Test
    public void getSessionMapByMacAddressInString() throws Exception {
        HashMap<String, DeviceInfo> map = (HashMap<String, DeviceInfo>) SessionUtil.getDeviceInfoByMacAddress(macInString);
        assertNotNull(map);
        assertEquals(mockSessionMap.size(), map.size());
        assertEquals(mockSessionMap.keySet(), map.keySet());
        for (Map.Entry<String, DeviceInfo> entry : map.entrySet()) {
            assertEquals(mockSessionMap.get(entry.getKey()), entry.getValue());
        }
    }

    @Test
    public void getSessionMapByMacAddressInByteArray() throws Exception {

        HashMap<String, DeviceInfo> map = (HashMap<String, DeviceInfo>) SessionUtil.getDeviceInfoByMacAddress(macInByte);
        assertNotNull(map);
        assertEquals(mockSessionMap.size(), map.size());
        assertEquals(mockSessionMap.keySet(), map.keySet());
        for (Map.Entry<String, DeviceInfo> entry : map.entrySet()) {
            assertEquals(mockSessionMap.get(entry.getKey()), entry.getValue());
        }
    }

    @Test
    public void getDeviceInfoByIoSession() throws Exception {
        DeviceInfo deviceInfo = SessionUtil.getDeviceInfoByIoSession(mockIoSession);
        assertEquals(mockDeviceInfo, deviceInfo);
    }

    @Test
    public void getSessionMapByCenterIdInString() throws Exception {

        HashMap<String, DeviceInfo> map = (HashMap<String, DeviceInfo>) SessionUtil.getDeviceInfoByCenterId(centerIdInString);
        assertNotNull(map);
        assertEquals(mockSessionMap.size(), map.size());
        assertEquals(mockSessionMap.keySet(), map.keySet());
        for (Map.Entry<String, DeviceInfo> entry : map.entrySet()) {
            assertEquals(mockSessionMap.get(entry.getKey()), entry.getValue());
        }
    }

    @Test
    public void getSessionMapByCenterIdInByteArray() throws Exception {
        HashMap<String, DeviceInfo> map = (HashMap<String, DeviceInfo>) SessionUtil.getDeviceInfoByCenterId(centerIdInByte);
        assertNotNull(map);
        assertEquals(mockSessionMap.size(), map.size());
        assertEquals(mockSessionMap.keySet(), map.keySet());
        for (Map.Entry<String, DeviceInfo> entry : map.entrySet()) {
            assertEquals(mockSessionMap.get(entry.getKey()), entry.getValue());
        }
    }

    @Test
    public void createSessionKeyWithCenterIdAndMacAddressSuccessfully() throws Exception {
        String expectResult = centerIdInString + macInString;
        assertEquals(expectResult, SessionUtil.createSessionKey(centerIdInByte, macInByte));
    }

    @Test
    public void sessionCouldBeRemoved() throws Exception {
        Initialization.getInstance().getClientMap().clear();
        DeviceInfo deviceInfo = mock(DeviceInfo.class);
        IoSession ioSession = mock(IoSession.class);
        IoSession onLineSession = mock(IoSession.class);
        when(deviceInfo.getSession()).thenReturn(ioSession);
        DeviceInfo onlineDevice = mock(DeviceInfo.class);
        when(onlineDevice.getSession()).thenReturn(onLineSession);
        Initialization.getInstance().getClientMap().put("1", deviceInfo);
        Initialization.getInstance().getClientMap().put("2", onlineDevice);
        SessionUtil.removeSession(ioSession);
        assertThat(Initialization.getInstance().getClientMap().size(), is(1));
        assertThat(Initialization.getInstance().getClientMap().get("2"), is(onlineDevice));
    }

    @Test
    public void getMacFromSessionKeyByCenterId() throws Exception {
        String mac = "fffffffffff0";
        String centerId = "00010203";
        String sessionKey = SessionUtil.createSessionKey(ByteArrayUtil.hexStringToByteArray(centerId), ByteArrayUtil.hexStringToByteArray(mac));

        String actualMac = SessionUtil.getMacFromSessionKey(sessionKey, centerId);

        assertEquals(mac, actualMac);

    }
}