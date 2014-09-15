package com.gamecenter.handler;

import java.io.IOException;

/**
 * Created by Chevis on 14-9-10.
 */
public interface TcpHandler {

    byte[] handle(byte[] request) throws IllegalAccessException, NoSuchFieldException, IOException;
}
