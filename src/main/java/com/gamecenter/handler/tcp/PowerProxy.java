package com.gamecenter.handler.tcp;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.gamecenter.handler.HttpJsonHandler;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.HttpRequestMessage;
import com.gamecenter.model.HttpResponseMessage;
import com.gamecenter.utils.MessageUtil;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.constants.MessageType;
import org.gamecenter.serializer.messages.MessageHeader;
import org.gamecenter.serializer.messages.downStream.PowerControlRequest;
import org.gamecenter.serializer.messages.downStream.PowerStatusRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Chevis on 14-10-3.
 */
public class PowerProxy extends DeviceProxy implements HttpJsonHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public void powerControl(DeviceInfo deviceInfo, boolean powerOn) {

        PowerControlRequest request = new PowerControlRequest();
        MessageHeader header = deviceInfo.getHeaderWithMessageNumIncreasment();
        header.setMsgType(MessageType.PowerControlRequest);
        request.setHeader(header);

        request.setSwitcher(MessageUtil.isPowerOn(powerOn));

        execute(request.build(), deviceInfo.getSession());
        logger.info("Message({}) sent: {}", ByteArrayUtil.toHexString(request.build()), request.toString());


    }

    public void queryPowerStatus(DeviceInfo deviceInfo) {

        PowerStatusRequest request = new PowerStatusRequest();
        MessageHeader header = deviceInfo.getHeaderWithMessageNumIncreasment();
        header.setMsgType(MessageType.PowerStatusRequest);
        request.setHeader(header);

        execute(request.build(), deviceInfo.getSession());
        logger.info("Message({}) sent: {}", ByteArrayUtil.toHexString(request.build()), request.toString());

    }

    @Override
    IoFuture execute(byte[] message, IoSession ioSession) {
        WriteFuture writeFuture = ioSession.write(message);

        return writeFuture;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {
        return null;
    }
}
