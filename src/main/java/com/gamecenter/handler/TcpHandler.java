package com.gamecenter.handler;

import org.apache.mina.core.session.IoSession;

import java.io.IOException;

/**
 * Created by Chevis on 14-9-10.
 */
public interface TcpHandler {
    byte[] handle(byte[] request, IoSession session) throws IllegalAccessException, NoSuchFieldException, IOException;
}
