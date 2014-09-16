package com.gamecenter.handler.tcp;

import com.gamecenter.handler.TcpHandler;
import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.messages.upStream.RuntimeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Boss on 2014/9/16.
 */
public class RuntimeHandler implements TcpHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public byte[] handle(byte[] resp, IoSession session) throws IllegalAccessException, NoSuchFieldException, IOException {

        RuntimeResponse response = new RuntimeResponse();
        response.parse(resp);

        logger.info("The device runs {} mins.", response.getRuntime());

        return null;
    }
}
