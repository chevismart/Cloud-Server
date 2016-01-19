package com.gamecenter.handler.tcp;

import com.gamecenter.handler.queue.QueueEntry;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.Model;
import com.gamecenter.utils.MessageUtil;
import org.gamecenter.serializer.messages.MessageHeader;
import org.gamecenter.serializer.messages.downStream.PowerControlRequest;
import org.gamecenter.serializer.messages.downStream.PowerStatusRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.gamecenter.serializer.constants.MessageType.PowerControlRequest;
import static org.gamecenter.serializer.constants.MessageType.PowerStatusRequest;

/**
 * Created by Chevis on 14-10-3.
 */
public class PowerProxy extends DeviceProxy {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Executor executor;

    public PowerProxy(Executor executor) {
        this.executor = executor;
    }

    public QueueEntry<Model> powerControl(DeviceInfo deviceInfo, boolean powerOn) {

        PowerControlRequest request = new PowerControlRequest();
        MessageHeader header = deviceInfo.getHeaderWithMessageNumIncreasment();
        header.setMsgType(PowerControlRequest);
        request.setHeader(header);
        request.setSwitcher(MessageUtil.isPowerOn(powerOn));

        return executor.execute(request.build(), PowerControlRequest, deviceInfo);
    }

    public QueueEntry<Model> queryPowerStatus(DeviceInfo deviceInfo) {

        PowerStatusRequest request = new PowerStatusRequest();
        MessageHeader header = deviceInfo.getHeaderWithMessageNumIncreasment();
        header.setMsgType(PowerStatusRequest);
        request.setHeader(header);

        return executor.execute(request.build(), PowerStatusRequest, deviceInfo);
    }
}
