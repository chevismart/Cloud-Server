package com.gamecenter.utils;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.gamecenter.model.Initialization;
import org.apache.mina.core.session.IoSession;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class SessionUtilTest {

    byte[] macInByte = new byte[]{(byte) 0xf0, (byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4, (byte) 0xff};
    byte[] centerIdInByte = new byte[]{00, 01, 02, 03};
    String macInString = ByteArrayUtil.toHexString(macInByte);
    String centerIdInString = ByteArrayUtil.toHexString(centerIdInByte);


    IoSession mockSession = mock(IoSession.class);
    HashMap<String, IoSession> mockSessionMap = new HashMap<String, IoSession>();

    @Before
    public void setUp() throws Exception {
        HashMap<String, IoSession> sessionHashMap = Initialization.getInstance().getClientMap();
        sessionHashMap.put(SessionUtil.createSessionKey(centerIdInByte, macInByte), mockSession);
        mockSessionMap.put(SessionUtil.createSessionKey(centerIdInByte, macInByte), mockSession);
    }

    @Test
    public void getSessionMapByMacAddressInString() throws Exception {

        HashMap<String, IoSession> map = (HashMap<String, IoSession>) SessionUtil.getSessionByMacAddress(macInString);
        assertNotNull(map);
        assertEquals(mockSessionMap.size(), map.size());
        assertEquals(mockSessionMap.keySet(), map.keySet());
        for (Map.Entry<String, IoSession> entry : map.entrySet()) {
            assertEquals(mockSessionMap.get(entry.getKey()), entry.getValue());
        }
    }

    @Test
    public void getSessionMapByMacAddressInByteArray() throws Exception {

        HashMap<String, IoSession> map = (HashMap<String, IoSession>) SessionUtil.getSessionByMacAddress(macInByte);
        assertNotNull(map);
        assertEquals(mockSessionMap.size(), map.size());
        assertEquals(mockSessionMap.keySet(), map.keySet());
        for (Map.Entry<String, IoSession> entry : map.entrySet()) {
            assertEquals(mockSessionMap.get(entry.getKey()), entry.getValue());
        }

    }


    @Test
    public void getSessionMapByCenterIdInString() throws Exception {

        HashMap<String, IoSession> map = (HashMap<String, IoSession>) SessionUtil.getSessionByCenterId(centerIdInString);
        assertNotNull(map);
        assertEquals(mockSessionMap.size(), map.size());
        assertEquals(mockSessionMap.keySet(), map.keySet());
        for (Map.Entry<String, IoSession> entry : map.entrySet()) {
            assertEquals(mockSessionMap.get(entry.getKey()), entry.getValue());
        }
    }

    @Test
    public void getSessionMapByCenterIdInByteArray() throws Exception {


        HashMap<String, IoSession> map = (HashMap<String, IoSession>) SessionUtil.getSessionByMacAddress(centerIdInByte);
        assertNotNull(map);
        assertEquals(mockSessionMap.size(), map.size());
        assertEquals(mockSessionMap.keySet(), map.keySet());
        for (Map.Entry<String, IoSession> entry : map.entrySet()) {
            assertEquals(mockSessionMap.get(entry.getKey()), entry.getValue());
        }
    }

    @Test
    public void createSessionKeyWithCenterIdAndMacAddressSuccessfully() throws Exception {
        String expectResult = centerIdInString + macInString;
        assertEquals(expectResult, SessionUtil.createSessionKey(centerIdInByte, macInByte));
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