package com.gamecenter.handler.tcp;

import com.gamecenter.handler.TcpHandler;
import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.messages.upStream.TopUpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Boss on 2014/9/16.
 */
public class TopUpHandler implements TcpHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public byte[] handle(byte[] resp, IoSession session) throws IllegalAccessException, NoSuchFieldException, IOException {

        TopUpResponse response = new TopUpResponse();
        response.parse(resp);

        logger.info("Reference No is {}, result is {}", response.getReferenceId(), response.getTopUpResult());

        return null;
    }
}
