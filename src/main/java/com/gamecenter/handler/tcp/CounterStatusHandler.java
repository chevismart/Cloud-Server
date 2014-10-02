package com.gamecenter.handler.tcp;

import com.gamecenter.handler.TcpHandler;
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

/**
 * Created by Boss on 2014/9/16.
 */
public class CounterStatusHandler implements TcpHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public byte[] handle(byte[] resp, IoSession session) throws IllegalAccessException, NoSuchFieldException, IOException {

        CounterStatusResponse response = new CounterStatusResponse();
        response.parse(resp);

//        HashMap<String, DeviceInfo> deviceSessionMap = Initialization.getInstance().getClientMap();
        DeviceInfo deviceInfo = SessionUtil.getDeviceInfoByIoSession(session);
        Counter counter = deviceInfo.getCounter();

        logger.info("Counter status is {}", response.getStatus());
        boolean isOn = MessageUtil.isStatusOn(response.getStatus());
        counter.setCoinOn(isOn);
        counter.setPrizeOn(isOn);
        counter.setLastStatusTime(new Date());

        return null;
    }
}
