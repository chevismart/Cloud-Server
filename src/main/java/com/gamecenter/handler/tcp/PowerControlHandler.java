package com.gamecenter.handler.tcp;

import com.gamecenter.handler.TcpHandler;
import com.gamecenter.handler.queue.Queues;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.Power;
import com.gamecenter.utils.MessageUtil;
import com.gamecenter.utils.SessionUtil;
import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.messages.upStream.PowerStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

import static org.gamecenter.serializer.constants.MessageType.PowerControlRequest;

public class PowerControlHandler implements TcpHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Queues queues;

    public PowerControlHandler(Queues queues) {
        this.queues = queues;
    }

    @Override
    public byte[] handle(byte[] resp, IoSession session) throws IllegalAccessException, NoSuchFieldException, IOException {
        PowerStatusResponse response = new PowerStatusResponse();
        response.parse(resp);

        logger.info("Power status response is {}", response.getStatus());

        // TODO: notify user

        DeviceInfo deviceInfo = SessionUtil.getDeviceInfoByIoSession(session);
        if (deviceInfo != null) {
            Power power = deviceInfo.getPower();
            power.setStatus(MessageUtil.isPowerOn(response.getStatus()));
            power.setUpdateTime(new Date());
            deviceInfo.setMessageHeader(response.getHeader());
            queues.consume(PowerControlRequest, deviceInfo.getMac(), power);
        }
        return null;
    }
}
