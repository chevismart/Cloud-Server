package com.gamecenter.handler.tcp;

import com.gamecenter.handler.HttpJsonHandler;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.HttpRequestMessage;
import com.gamecenter.model.HttpResponseMessage;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.constants.MessageType;
import org.gamecenter.serializer.messages.MessageHeader;
import org.gamecenter.serializer.messages.downStream.CounterRequest;
import org.gamecenter.serializer.messages.upStream.CounterStatusResponse;

/**
 * Created by Chevis on 14-9-19.
 */
public class CounterProxy extends DeviceProxy implements HttpJsonHandler {


    public CounterStatusResponse refreshCounterQty(boolean queryCoin, boolean queryPrize, DeviceInfo deviceInfo) {
        CounterRequest request = new CounterRequest();
        MessageHeader header = deviceInfo.getHeaderWithMessageNumIncreasment();
        header.setMsgType(MessageType.CounterRequest);
        request.setHeader(header);
        request.setReqCoin(queryCoin);
        request.setReqPrize(queryPrize);
        execute(request.build(), deviceInfo.getSession());
        return null;
//        return execute(request.build());
    }

    @Override
    synchronized IoFuture execute(byte[] message, IoSession ioSession) {

        WriteFuture writeFuture = ioSession.write(message);

        return writeFuture;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {
        return null;
    }
}
