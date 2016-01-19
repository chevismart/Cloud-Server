package com.gamecenter.handler.tcp;

import com.gamecenter.handler.TcpHandler;
import com.gamecenter.handler.queue.Queues;
import com.gamecenter.model.Counter;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.utils.MessageUtil;
import com.gamecenter.utils.SessionUtil;
import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.messages.upStream.CounterStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

import static org.gamecenter.serializer.constants.MessageType.CounterStatusRequest;

public class CounterStatusHandler implements TcpHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Queues queues;

    public CounterStatusHandler(Queues queues) {
        this.queues = queues;
    }

    @Override
    public byte[] handle(byte[] resp, IoSession session) throws IllegalAccessException, NoSuchFieldException, IOException {

        CounterStatusResponse response = new CounterStatusResponse();
        response.parse(resp);

        DeviceInfo deviceInfo = SessionUtil.getDeviceInfoByIoSession(session);
        Counter counter = deviceInfo.getCounter();

        logger.info("Counter status is {}", response.getStatus());
        boolean isOn = MessageUtil.isStatusOn(response.getStatus());
        counter.setCoinOn(isOn);
        counter.setPrizeOn(isOn);
        counter.setLastStatusTime(new Date());
        deviceInfo.setMessageHeader(response.getHeader());
        logger.debug("Counter is {}, mac is {}", counter, deviceInfo.getMac());
        queues.consume(CounterStatusRequest, deviceInfo.getMac(), counter);
        return null;
    }
}
