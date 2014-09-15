package com.gamecenter.handler.tcp;

import com.gamecenter.handler.TcpHandler;
import com.gamecenter.model.Initialization;
import com.gamecenter.utils.SessionUtil;
import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.constants.MessageType;
import org.gamecenter.serializer.messages.MessageHeader;
import org.gamecenter.serializer.messages.downStream.LoginResponse;
import org.gamecenter.serializer.messages.upStream.LoginRequest;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Chevis on 14-9-10.
 */
public class LoginHandler implements TcpHandler {
    @Override
    public byte[] handle(byte[] requestByte, IoSession session) throws IllegalAccessException, NoSuchFieldException, IOException {

        LoginRequest request = new LoginRequest();
        request.parse(requestByte);

        HashMap<String, IoSession> deviceSessionMap = Initialization.getInstance().getClientMap();

        // Store the session with the new created session key
        String sessionKey = SessionUtil.createSessionKey(request.getCenterId(), request.getMac());
        deviceSessionMap.put(sessionKey, session);

        LoginResponse response = new LoginResponse();

        MessageHeader header = request.getHeader();
        header.setMsgType(MessageType.LoginResponse);
        response.setHeader(header);


        return response.build();
    }
}
