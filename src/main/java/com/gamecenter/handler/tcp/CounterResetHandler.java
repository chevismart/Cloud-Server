package com.gamecenter.handler.tcp;

import com.gamecenter.handler.TcpHandler;
import com.gamecenter.model.Counter;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.utils.MessageUtil;
import com.gamecenter.utils.SessionUtil;
import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.messages.upStream.ResetCounterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Boss on 2014/9/16.
 */
public class CounterResetHandler implements TcpHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public byte[] handle(byte[] resp, IoSession session) throws IllegalAccessException, NoSuchFieldException, IOException {
        ResetCounterResponse response = new ResetCounterResponse();
        response.parse(resp);
        logger.info("Received reset counter: coin: {}, prize: {} ", response.getResetCoinResult(), response.getResetPrizeResult());

        DeviceInfo deviceInfo = SessionUtil.getDeviceInfoByIoSession(session);
        Counter counter = deviceInfo.getCounter();

        if (MessageUtil.isSuccess(response.getResetCoinResult())) counter.setLastCoinResetTime(new Date());

        if (MessageUtil.isSuccess(response.getResetPrizeResult())) counter.setLastPrizeResetTime(new Date());

        deviceInfo.setMessageHeader(response.getHeader());

        return null;
    }
}
