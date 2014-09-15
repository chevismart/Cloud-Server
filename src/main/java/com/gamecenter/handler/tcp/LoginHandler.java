package com.gamecenter.handler.tcp;

import com.gamecenter.handler.TcpHandler;
import org.gamecenter.serializer.constants.MessageType;
import org.gamecenter.serializer.messages.MessageHeader;
import org.gamecenter.serializer.messages.downStream.LoginResponse;
import org.gamecenter.serializer.messages.upStream.LoginRequest;

import java.io.IOException;

/**
 * Created by Chevis on 14-9-10.
 */
public class LoginHandler implements TcpHandler {
    @Override
    public byte[] handle(byte[] requestByte) throws IllegalAccessException, NoSuchFieldException, IOException {

        LoginRequest request = new LoginRequest();
        request.parse(requestByte);



        LoginResponse response = new LoginResponse();

        MessageHeader header = request.getHeader();
        header.setMsgType(MessageType.LoginResponse);
        response.setHeader(header);


        return response.build();
    }
}
