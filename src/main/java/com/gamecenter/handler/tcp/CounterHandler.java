package com.gamecenter.handler.tcp;

import com.gamecenter.handler.TcpHandler;
import com.gamecenter.handler.queue.Queues;
import com.gamecenter.model.Counter;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.utils.SessionUtil;
import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.messages.upStream.CounterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

import static org.gamecenter.serializer.constants.MessageType.CounterRequest;
import static org.gamecenter.serializer.constants.MessageType.CounterStatusRequest;

/**
 * Created by Boss on 2014/9/16.
 */
public class CounterHandler implements TcpHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Queues queues;

    public CounterHandler(Queues queues) {
        this.queues = queues;
    }

    @Override
    public byte[] handle(byte[] resp, IoSession session) throws IllegalAccessException, NoSuchFieldException, IOException {

        CounterResponse response = new CounterResponse();
        response.parse(resp);

        logger.info("Received coin qty is {}", response.getCoinQuantity());
        logger.info("Received prize qty is {}", response.getPrizeQuantity());

        DeviceInfo deviceInfo = SessionUtil.getDeviceInfoByIoSession(session);
        if (null != deviceInfo) {
            Counter counter = deviceInfo.getCounter();

            counter.setCoinQty(response.getCoinQuantity());
            counter.setPrizeQty(response.getPrizeQuantity());

            counter.setLastQtyTime(new Date());

            deviceInfo.setMessageHeader(response.getHeader());
            logger.debug("queues is: {}", queues);
            logger.debug("Device mac is: {}", deviceInfo.getMac());
            queues.consume(CounterRequest, deviceInfo.getMac(), counter);
        } else {
            logger.warn("Device not found from the session: [{}]", session.getId());
        }
        return null;
    }
}
