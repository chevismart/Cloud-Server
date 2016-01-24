package com.gamecenter.handler.tcp;

import com.gamecenter.handler.TcpHandler;
import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.messages.MessageHeader;
import org.gamecenter.serializer.messages.downStream.HeartbeatResponse;
import org.gamecenter.serializer.messages.upStream.HeartbeatRequest;

import java.io.IOException;

import static com.gamecenter.utils.SessionUtil.updateDeviceOnlineTime;
import static org.gamecenter.serializer.constants.MessageType.HeartbeatResponse;

public class HeartbeatHandler implements TcpHandler {
    @Override
    public byte[] handle(byte[] bytes, IoSession session) throws IllegalAccessException, NoSuchFieldException, IOException {

        HeartbeatRequest request = new HeartbeatRequest();
        request.parse(bytes);
        HeartbeatResponse response = new HeartbeatResponse();
        MessageHeader header = request.getHeader();
        header.setMsgType(HeartbeatResponse);
        response.setHeader(header);
        // Update the time that connecting with the device
        updateDeviceOnlineTime(session);
        return response.build();
    }
}
