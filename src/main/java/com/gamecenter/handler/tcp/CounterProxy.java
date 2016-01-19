package com.gamecenter.handler.tcp;

import com.gamecenter.handler.queue.QueueEntry;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.Model;
import com.gamecenter.model.TopUp;
import com.gamecenter.utils.MessageUtil;
import org.gamecenter.serializer.messages.MessageHeader;
import org.gamecenter.serializer.messages.downStream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.TimeoutException;

import static org.gamecenter.serializer.constants.MessageType.*;

public class CounterProxy extends DeviceProxy {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Executor executor;

    public CounterProxy(Executor executor) {
        this.executor = executor;
    }

    public QueueEntry<Model> switchCounter(DeviceInfo deviceInfo, boolean isPowerOn) {
        CounterSwitchRequest request = new CounterSwitchRequest();
        MessageHeader header = deviceInfo.getHeaderWithMessageNumIncreasment();
        header.setMsgType(CounterSwitchRequest);
        request.setHeader(header);
        request.setSwitcher(MessageUtil.isEnable(isPowerOn));
        return executor.execute(request.build(), CounterSwitchRequest, deviceInfo);
    }

    public QueueEntry<Model> topUpCoins(DeviceInfo deviceInfo, int coinQty, String referenceId) {
        TopUpRequest request = new TopUpRequest();
        MessageHeader header = deviceInfo.getHeaderWithMessageNumIncreasment();
        header.setMsgType(TopUpRequest);
        request.setHeader(header);

        request.setReferenceId(referenceId);
        request.setTopUpQuantity(coinQty);

        TopUp topUp = new TopUp(new Date());
        topUp.setReferenceId(request.getReferenceId());
        topUp.setCoinQty(request.getTopUpQuantity());
        topUp.setTopUpResult(false);

        deviceInfo.getTopUpHistory().put(request.getReferenceId(), topUp);
        return executor.execute(request.build(), TopUpRequest, deviceInfo);
    }

    public QueueEntry<Model> resetCounter(boolean resetCoin, boolean resetPrize, DeviceInfo deviceInfo) {
        ResetCounterRequest request = new ResetCounterRequest();
        MessageHeader header = deviceInfo.getHeaderWithMessageNumIncreasment();
        header.setMsgType(ResetCounterRequest);
        request.setHeader(header);
        request.setResetCoin(MessageUtil.isEnable(resetCoin));
        request.setResetPrize(MessageUtil.isEnable(resetPrize));
        return executor.execute(request.build(), ResetCounterRequest, deviceInfo);
    }

    public QueueEntry<Model> refreshCounterStatus(DeviceInfo deviceInfo) {
        CounterStatusRequest request = new CounterStatusRequest();
        MessageHeader header = deviceInfo.getHeaderWithMessageNumIncreasment();
        header.setMsgType(CounterStatusRequest);
        request.setHeader(header);
        return executor.execute(request.build(), CounterStatusRequest, deviceInfo);
    }


    public QueueEntry<Model> refreshCounterQty(boolean queryCoin, boolean queryPrize, DeviceInfo deviceInfo) throws TimeoutException {
        CounterRequest request = new CounterRequest();
        MessageHeader header = deviceInfo.getHeaderWithMessageNumIncreasment();
        header.setMsgType(CounterRequest);
        request.setHeader(header);
        request.setReqCoin(MessageUtil.isEnable(queryCoin));
        request.setReqPrize(MessageUtil.isEnable(queryPrize));
        return executor.execute(request.build(), CounterRequest, deviceInfo);
    }
}
