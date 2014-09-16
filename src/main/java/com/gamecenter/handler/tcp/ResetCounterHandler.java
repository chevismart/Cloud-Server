package com.gamecenter.handler.tcp;

import com.gamecenter.handler.TcpHandler;
import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.messages.upStream.ResetCounterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Boss on 2014/9/16.
 */
public class ResetCounterHandler implements TcpHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public byte[] handle(byte[] resp, IoSession session) throws IllegalAccessException, NoSuchFieldException, IOException {
        ResetCounterResponse response = new ResetCounterResponse();
        response.parse(resp);
        logger.info("Received reset counter: coin: {}, prize: {} ", response.getResetCoinResult(), response.getResetPrizeResult());
        return null;
    }
}
