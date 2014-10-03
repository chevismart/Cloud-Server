package com.gamecenter.handler.tcp;

import com.gamecenter.handler.HttpJsonHandler;
import com.gamecenter.model.HttpRequestMessage;
import com.gamecenter.model.HttpResponseMessage;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.session.IoSession;

/**
 * Created by Chevis on 14-10-3.
 */
public class PowerProxy extends DeviceProxy implements HttpJsonHandler {
    @Override
    IoFuture execute(byte[] message, IoSession ioSession) {
        return null;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {
        return null;
    }
}
