package com.gamecenter.handler.tcp;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.gamecenter.handler.HttpJsonHandler;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.HttpRequestMessage;
import com.gamecenter.model.HttpResponseMessage;
import com.gamecenter.model.TopUp;
import com.gamecenter.utils.MessageUtil;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.constants.MessageType;
import org.gamecenter.serializer.messages.MessageHeader;
import org.gamecenter.serializer.messages.downStream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by Chevis on 14-9-19.
 */
public class CounterProxy extends DeviceProxy implements HttpJsonHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public void switchCounter(DeviceInfo deviceInfo, boolean isPowerOn) {
        CounterSwitchRequest request = new CounterSwitchRequest();
        MessageHeader header = deviceInfo.getHeaderWithMessageNumIncreasment();
        header.setMsgType(MessageType.CounterSwitchRequest);
        request.setHeader(header);

        request.setSwitcher(MessageUtil.isEnable(isPowerOn));

        execute(request.build(), deviceInfo.getSession());
        logger.info("Message({}) sent: {}", ByteArrayUtil.toHexString(request.build()), request.toString());
    }

    public void topUpCoins(DeviceInfo deviceInfo, int coinQty, String referenceId, Date time) {
        TopUpRequest request = new TopUpRequest();
        MessageHeader header = deviceInfo.getHeaderWithMessageNumIncreasment();
        header.setMsgType(MessageType.TopUpRequest);
        request.setHeader(header);

        request.setReferenceId(referenceId);
        request.setTopUpQuantity(coinQty);

        TopUp topUp = new TopUp();
        topUp.setReferenceId(request.getReferenceId());
        topUp.setCoinQty(request.getTopUpQuantity());
        topUp.setTopUpResult(false);
        topUp.setUpdateTime(time);

        deviceInfo.getTopUpHistory().put(request.getReferenceId(), topUp);

        execute(request.build(), deviceInfo.getSession());
        logger.info("Message({}) sent: {}", ByteArrayUtil.toHexString(request.build()), request.toString());
    }

    public void resetCounter(boolean resetCoin, boolean resetPrize, DeviceInfo deviceInfo) {
        ResetCounterRequest request = new ResetCounterRequest();
        MessageHeader header = deviceInfo.getHeaderWithMessageNumIncreasment();
        header.setMsgType(MessageType.ResetCounterRequest);
        request.setHeader(header);

        request.setResetCoin(MessageUtil.isEnable(resetCoin));
        request.setResetPrize(MessageUtil.isEnable(resetPrize));

        execute(request.build(), deviceInfo.getSession());
        logger.info("Message({}) sent: {}", ByteArrayUtil.toHexString(request.build()), request.toString());
    }

    public void refreshCounterStatus(DeviceInfo deviceInfo) {
        CounterStatusRequest request = new CounterStatusRequest();
        MessageHeader header = deviceInfo.getHeaderWithMessageNumIncreasment();
        header.setMsgType(MessageType.CounterStatusRequest);
        request.setHeader(header);

        execute(request.build(), deviceInfo.getSession());
        logger.info("Message({}) sent: {}", ByteArrayUtil.toHexString(request.build()), request.toString());
    }


    public void refreshCounterQty(boolean queryCoin, boolean queryPrize, DeviceInfo deviceInfo) {
        CounterRequest request = new CounterRequest();
        MessageHeader header = deviceInfo.getHeaderWithMessageNumIncreasment();
        header.setMsgType(MessageType.CounterRequest);
        request.setHeader(header);
        request.setReqCoin(MessageUtil.isEnable(queryCoin));
        request.setReqPrize(MessageUtil.isEnable(queryPrize));
        execute(request.build(), deviceInfo.getSession());
        logger.info("Message({}) sent: {}", ByteArrayUtil.toHexString(request.build()), request.toString());
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
