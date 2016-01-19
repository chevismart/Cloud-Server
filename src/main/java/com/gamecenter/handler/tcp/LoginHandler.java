package com.gamecenter.handler.tcp;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.gamecenter.handler.TcpHandler;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.utils.SessionUtil;
import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.messages.MessageHeader;
import org.gamecenter.serializer.messages.downStream.LoginResponse;
import org.gamecenter.serializer.messages.upStream.LoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

import static com.gamecenter.model.Initialization.getInstance;
import static org.gamecenter.serializer.constants.MessageType.LoginResponse;

public class LoginHandler implements TcpHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public byte[] handle(byte[] requestByte, IoSession session) throws IllegalAccessException, NoSuchFieldException, IOException {

        LoginRequest request = new LoginRequest();
        request.parse(requestByte);

        HashMap<String, DeviceInfo> deviceSessionMap = getInstance().getClientMap();

        byte[] mac = request.getMac();
        if (null != mac && mac.length != 0) {
            // Store the session with the new created session key
            String sessionKey = SessionUtil.createSessionKey(request.getCenterId(), mac);

            DeviceInfo deviceInfo = new DeviceInfo(ByteArrayUtil.toHexString(mac));
            deviceInfo.setSession(session);
            deviceInfo.setMessageHeader(request.getHeader());

            deviceSessionMap.put(sessionKey, deviceInfo);

            LoginResponse response = new LoginResponse();

            MessageHeader header = request.getHeader();
            header.setMsgType(LoginResponse);
            response.setHeader(header);

            logger.info("Login successfully for {}", ByteArrayUtil.toHexString(mac));
            return response.build();
        } else {
            logger.warn("Login failed since no MAC address found! ");
            return null;
        }
    }
}
