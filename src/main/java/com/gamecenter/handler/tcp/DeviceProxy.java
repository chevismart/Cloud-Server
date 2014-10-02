package com.gamecenter.handler.tcp;

import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.session.IoSession;

/**
 * Created by Chevis on 14-9-19.
 */
public abstract class DeviceProxy {

    abstract IoFuture execute(byte[] message, IoSession ioSession);
}
