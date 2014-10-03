package com.gamecenter.handler.tcp;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.gamecenter.handler.TcpHandler;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.Initialization;
import com.gamecenter.utils.SessionUtil;
import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.constants.MessageType;
import org.gamecenter.serializer.messages.MessageHeader;
import org.gamecenter.serializer.messages.downStream.LoginResponse;
import org.gamecenter.serializer.messages.upStream.LoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Chevis on 14-9-10.
 */
public class LoginHandler implements TcpHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public byte[] handle(byte[] requestByte, IoSession session) throws IllegalAccessException, NoSuchFieldException, IOException {

        LoginRequest request = new LoginRequest();
        request.parse(requestByte);

        HashMap<String, DeviceInfo> deviceSessionMap = Initialization.getInstance().getClientMap();

        byte[] mac = request.getMac();
        if (null != mac && mac.length != 0) {
            // Store the session with the new created session key
            String sessionKey = SessionUtil.createSessionKey(request.getCenterId(), request.getMac());

            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setSession(session);
            deviceInfo.setMessageHeader(request.getHeader());

            deviceSessionMap.put(sessionKey, deviceInfo);

            LoginResponse response = new LoginResponse();

            MessageHeader header = request.getHeader();
            header.setMsgType(MessageType.LoginResponse);
            response.setHeader(header);

            logger.info("Login successfully for {}", ByteArrayUtil.toHexString(mac));
            return response.build();
        } else {
            logger.warn("Login failed since no MAC address found! ");
            return null;
        }
    }
}
